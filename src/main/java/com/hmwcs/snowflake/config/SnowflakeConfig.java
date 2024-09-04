package com.hmwcs.snowflake.config;

/**
 * Configuration class for the Snowflake ID generator.
 * This class contains all the constants and settings used in generating Snowflake IDs.
 */
public class SnowflakeConfig {
    /**
     * The custom epoch used for generating timestamps in the Snowflake ID.
     * Represents the time in milliseconds since 2024/09/01.
     */
    public static final long EPOCH = 1725148800000L;

    /**
     * The number of bits allocated for the data center ID in the Snowflake ID.
     */
    public static final long DATA_CENTER_ID_BITS = 5L;

    /**
     * The number of bits allocated for the machine ID in the Snowflake ID.
     */
    public static final long MACHINE_ID_BITS = 5L;

    /**
     * The maximum value for the data center ID, calculated based on the number of bits allocated.
     */
    public static final long MAX_DATA_CENTER_ID = -1L ^ (-1L << DATA_CENTER_ID_BITS);

    /**
     * The maximum value for the machine ID, calculated based on the number of bits allocated.
     */
    public static final long MAX_MACHINE_ID = -1L ^ (-1L << MACHINE_ID_BITS);

    /**
     * The number of bits allocated for the sequence number in the Snowflake ID.
     */
    public static final long SEQUENCE_BITS = 12L;

    /**
     * The number of bits to shift the machine ID to the left in the final Snowflake ID.
     */
    public static final long MACHINE_ID_SHIFT = SEQUENCE_BITS;

    /**
     * The number of bits to shift the data center ID to the left in the final Snowflake ID.
     */
    public static final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + MACHINE_ID_BITS;

    /**
     * The number of bits to shift the timestamp to the left in the final Snowflake ID.
     */
    public static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + MACHINE_ID_BITS + DATA_CENTER_ID_BITS;

    /**
     * A mask used to ensure the sequence number fits within the allocated number of bits.
     */
    public static final long SEQUENCE_MASK = -1L ^ (-1L << SEQUENCE_BITS);
}
