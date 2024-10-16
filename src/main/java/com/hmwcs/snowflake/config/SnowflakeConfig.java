package com.hmwcs.snowflake.config;

/**
 * Configuration class for the Snowflake ID generator.
 * This class contains all the constants and settings used in generating Snowflake IDs.
 */
public class SnowflakeConfig {
    /**
     * The default epoch used for generating timestamps in the Snowflake ID.
     * Represents the time in milliseconds since 2024/09/01.
     * <p>
     * The timestamp uses 41 bits in the Snowflake ID, capable of representing up to 69.7 years.
     */
    public static final long DEFAULT_EPOCH = 1725148800000L;

    /**
     * The number of bits allocated for the data center ID in the Snowflake ID.
     */
    public static final int DATA_CENTER_ID_BITS = 5;

    /**
     * The number of bits allocated for the machine ID in the Snowflake ID.
     */
    public static final int MACHINE_ID_BITS = 5;

    /**
     * The maximum value for the data center ID, calculated based on the number of bits allocated.
     */
    public static final int MAX_DATA_CENTER_ID = ~(-1 << DATA_CENTER_ID_BITS);

    /**
     * The maximum value for the machine ID, calculated based on the number of bits allocated.
     */
    public static final int MAX_MACHINE_ID = ~(-1 << MACHINE_ID_BITS);

    /**
     * The number of bits allocated for the sequence number in the Snowflake ID.
     */
    public static final int SEQUENCE_BITS = 12;

    /**
     * The number of bits to shift the machine ID to the left in the final Snowflake ID.
     */
    public static final int MACHINE_ID_SHIFT = SEQUENCE_BITS;

    /**
     * The number of bits to shift the data center ID to the left in the final Snowflake ID.
     */
    public static final int DATA_CENTER_ID_SHIFT = MACHINE_ID_SHIFT + MACHINE_ID_BITS;

    /**
     * The number of bits to shift the timestamp to the left in the final Snowflake ID.
     */
    public static final int TIMESTAMP_LEFT_SHIFT = DATA_CENTER_ID_SHIFT + DATA_CENTER_ID_BITS;

    /**
     * A mask used to ensure the sequence number fits within the allocated number of bits.
     */
    public static final int SEQUENCE_MASK = ~(-1 << SEQUENCE_BITS);

    /**
     * Tolerance window for clock rollback (in milliseconds)
     */
    public static final int CLOCK_BACKWARD_TOLERANCE = 50;
}
