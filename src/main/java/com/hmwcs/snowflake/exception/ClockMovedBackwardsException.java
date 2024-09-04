package com.hmwcs.snowflake.exception;

/**
 * Exception thrown when the system clock is detected to have moved backwards.
 */
public class ClockMovedBackwardsException  extends RuntimeException {
    /**
     * Constructs a new exception indicating that the clock has moved backwards.
     *
     * @param lastTimestamp the last timestamp when the ID was generated
     * @param currentTimestamp the current timestamp that is earlier than the last timestamp
     */
    public ClockMovedBackwardsException(long lastTimestamp, long currentTimestamp) {
        super(String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - currentTimestamp));
    }
}
