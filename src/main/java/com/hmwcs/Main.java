package com.hmwcs;

import com.hmwcs.snowflake.generator.SnowflakeIdGenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(1, 19); // Example data center ID: 1, machine ID: 8
        long uniqueId = idGenerator.nextId();

        System.out.println("Generated Snowflake ID: " + uniqueId);
        System.out.println("In Binary Form: " + Long.toBinaryString(uniqueId));
        System.out.println();
    }
}
