package com.hmwcs.snowflake;

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
}
