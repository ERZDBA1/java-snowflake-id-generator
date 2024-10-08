package com.hmwcs.snowflake.generator;

import com.hmwcs.snowflake.exception.ClockMovedBackwardsException;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

import static com.hmwcs.snowflake.config.SnowflakeConfig.*;
import static java.lang.System.currentTimeMillis;

/**
 * Generates unique IDs using the Twitter Snowflake algorithm.
 *
 * <p>
 * Snowflake ID Structure (Twitter’s original):
 * </p>
 *
 * <ul>
 * <li>64 bits total:</li>
 * <li>Sign Bit (1 bit): Reserved for the sign (1 bit), ensuring the ID fits in a 64-bit unsigned integer.</li>
 * <li>Timestamp (41 bits): Represents the time in milliseconds since a custom epoch.</li>
 * <li>Data Center ID (5 bits): Identifies the data center.</li>
 * <li>Machine ID (5 bits): Identifies the specific machine within the data center.</li>
 * <li>Sequence Number (12 bits): A counter that increments for IDs generated within the same millisecond.</li>
 * </ul>
 *
 * <p>
 * For more information, see the original design details at
 * <a href="https://blog.twitter.com/engineering/en_us/a/2010/announcing-snowflake">Twitter Engineering Blog</a>.
 * </p>
 */
public class SnowflakeIdGenerator {
    private final int dataCenterId; // ID of the data center
    private final int machineId; // ID of the machine
    private final long epoch; // The epoch used for ID generation

    private record State(long timestamp, int sequence) {
    }

    // Atomic reference to the current state
    private final AtomicReference<State> atomicState = new AtomicReference<>(new State(-1L, 0));

    /**
     * Constructor to initialize the Snowflake ID generator with default epoch.
     *
     * @param dataCenterId The ID of the data center (0-31). Cannot be null.
     * @param machineId The ID of the machine (0-31). Cannot be null.
     * @throws IllegalArgumentException if the dataCenterId or machineId is out of range
     */
    public SnowflakeIdGenerator(int dataCenterId, int machineId) {
        this(dataCenterId, machineId, DEFAULT_EPOCH);
    }

    /**
     * Constructor to initialize the Snowflake ID generator with a custom epoch.
     *
     * @param dataCenterId The ID of the data center (0-31). Cannot be null.
     * @param machineId The ID of the machine (0-31). Cannot be null.
     * @param customEpoch The custom epoch to use for generating IDs
     * @throws IllegalArgumentException if the dataCenterId or machineId is out of range
     */
    public SnowflakeIdGenerator(int dataCenterId, int machineId, long customEpoch) {
        if (customEpoch > currentTimeMillis())
            throw new IllegalArgumentException("Custom epoch cannot be in the future");

        if (dataCenterId > MAX_DATA_CENTER_ID || dataCenterId < 0)
            throw new IllegalArgumentException(
                    String.format("Data Center ID (%d) must be between 0 and %d", dataCenterId, MAX_DATA_CENTER_ID));

        if (machineId > MAX_MACHINE_ID || machineId < 0)
            throw new IllegalArgumentException(
                    String.format("Machine ID (%d) must be between 0 and %d", machineId, MAX_MACHINE_ID));

        this.dataCenterId = dataCenterId;
        this.machineId = machineId;
        epoch = customEpoch;
    }

    /**
     * Generates a unique Snowflake ID.
     *
     * @return A unique 64-bit Snowflake ID
     * @throws ClockMovedBackwardsException if the system clock moves backwards
     */
    public long nextId() {
        while (true) {
            State currentState = atomicState.get();
            long timestamp = currentTimeMillis();

            if (timestamp < currentState.timestamp)
                throw new ClockMovedBackwardsException(currentState.timestamp, timestamp);

            int sequence = currentState.sequence;

            if (timestamp == currentState.timestamp) {
                sequence = (sequence + 1) & SEQUENCE_MASK;
                if (sequence == 0)
                    timestamp = waitNextMillis(timestamp);
            } else sequence = 0;

            State newState = new State(timestamp, sequence);

            if (atomicState.compareAndSet(currentState, newState))
                return generateId(timestamp, sequence);
            // If CAS fails, another thread has updated the state; retry
            LockSupport.parkNanos(1000);
        }
    }

    /**
     * Constructs a unique Snowflake ID based on the timestamp, data center ID, machine ID, and sequence number.
     *
     * <p>
     * The generated ID is composed by left-shifting the timestamp and combining it with the data center ID, machine ID,
     * and sequence number using bitwise OR operations.
     * </p>
     *
     * @param timestamp The current timestamp in milliseconds
     * @param sequence The sequence number for the current millisecond
     * @return A unique 64-bit Snowflake ID
     */
    private long generateId(long timestamp, int sequence) {
        return ((timestamp - epoch) << TIMESTAMP_LEFT_SHIFT) |
                ((long) dataCenterId << DATA_CENTER_ID_SHIFT) |
                ((long) machineId << MACHINE_ID_SHIFT) |
                (sequence & SEQUENCE_MASK);
    }

    /**
     * Waits for the next millisecond if the clock has not advanced.
     *
     * @param lastTimestamp The timestamp of the last ID generated
     * @return The current timestamp
     * @throws ClockMovedBackwardsException if the system clock moves backwards
     */
    private long waitNextMillis(long lastTimestamp) {
        long timestamp = currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            LockSupport.parkNanos(1000); // Hint to the scheduler that the thread is willing to yield its current use of a processor
            if (timestamp < lastTimestamp)
                throw new ClockMovedBackwardsException(lastTimestamp, timestamp);
            else timestamp = currentTimeMillis();
        }

        return timestamp;
    }
}
