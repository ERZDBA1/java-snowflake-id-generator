package com.hmwcs;

import com.hmwcs.snowflake.generator.SnowflakeIdGenerator;

public class Main {
    public static void main(String[] args) {
        SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(0, 0); // Example data center ID: 1, machine ID: 1
        long uniqueId = idGenerator.nextId();

        System.out.println("Generated Snowflake ID: " + uniqueId);
        System.out.println("In Binary Form: " + Long.toBinaryString(uniqueId));
    }
}
