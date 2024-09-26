package com.hmwcs.snowflake.generator;

import com.hmwcs.snowflake.exception.ClockMovedBackwardsException;

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
    private final int DATA_CENTER_ID; // ID of the data center
    private final int MACHINE_ID; // ID of the machine
    private final long EPOCH; // The epoch used for ID generation

    private int sequence = 0; // Sequence number
    private long lastTimestamp = -1L; // Last timestamp for generating IDs

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
        if (dataCenterId > MAX_DATA_CENTER_ID || dataCenterId < 0)
            throw new IllegalArgumentException(
                    String.format("DataCenter ID can't be greater than %d or less than 0", MAX_DATA_CENTER_ID));

        if (machineId > MAX_MACHINE_ID || machineId < 0)
            throw new IllegalArgumentException(
                    String.format("Machine ID can't be greater than %d or less than 0", MAX_MACHINE_ID));

        DATA_CENTER_ID = dataCenterId;
        MACHINE_ID = machineId;
        EPOCH = customEpoch;
    }

    /**
     * Generates a unique Snowflake ID.
     *
     * @return A unique 64-bit Snowflake ID
     * @throws ClockMovedBackwardsException if the system clock moves backwards
     */
    public synchronized long nextId() {
        long timestamp = currentTimeMillis();

        if (timestamp < lastTimestamp)
            throw new ClockMovedBackwardsException(lastTimestamp, timestamp);

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0)
                timestamp = waitNextMillis(lastTimestamp);
        } else sequence = 0;

        lastTimestamp = timestamp;

        return generateId(timestamp, sequence);
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
        return ((timestamp - EPOCH) << TIMESTAMP_LEFT_SHIFT) |
                ((long) DATA_CENTER_ID << DATA_CENTER_ID_SHIFT) |
                ((long) MACHINE_ID << MACHINE_ID_SHIFT) |
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
        while (timestamp <= lastTimestamp)
            if (timestamp < lastTimestamp)
                throw new ClockMovedBackwardsException(lastTimestamp, timestamp);
            else timestamp = currentTimeMillis();

        return timestamp;
    }
}
