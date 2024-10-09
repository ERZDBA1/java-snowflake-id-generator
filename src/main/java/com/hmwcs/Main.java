package com.hmwcs;

import com.hmwcs.snowflake.generator.SnowflakeIdGenerator;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(0, 0); // Example data center ID: 1, machine ID: 1
        long uniqueId = idGenerator.nextId();

        System.out.println("Generated Snowflake ID: " + uniqueId);
        System.out.println("In Binary Form: " + Long.toBinaryString(uniqueId));
        System.out.println();

        benchmarkTest();
        System.out.println();
        concurrentBenchmarkTest();
    }

    public static void benchmarkTest() {
        int dataCenterId = 1;
        int machineId = 1;
        int iterations = 20_000_000; // Number of IDs to generate for the benchmark

        SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(dataCenterId, machineId);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < iterations; i++) {
            idGenerator.nextId();
        }

        long endTime = System.currentTimeMillis();;
        double durationInSeconds = (endTime - startTime) / 1_000.0;

        System.out.println("Generated " + iterations + " IDs in " + durationInSeconds + " seconds.");
    }

    public static void concurrentBenchmarkTest() {
        int dataCenterId = 1;
        int machineId = 1;
        int numThreads = 10; // Number of threads to use for the test
        int idsPerThread = 2_000_000; // Number of IDs each thread should generate

        SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(dataCenterId, machineId);
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        // Set<Long> uniqueIds = new HashSet<>(); // To store unique IDs

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < idsPerThread; j++) {
                    long id = idGenerator.nextId();
                    // synchronized (uniqueIds) { // Ensure thread-safe addition to the set
                    //    if (!uniqueIds.add(id)) { // Check for duplicates
                    //        throw new RuntimeException("Duplicate ID detected: " + id);
                    //    }
                    //}
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.HOURS); // Wait for all tasks to complete or timeout after 1 hour
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Concurrent test interrupted.");
        }

        long endTime = System.currentTimeMillis();
        double durationInSeconds = (endTime - startTime) / 1_000.0;

        System.out.println("Concurrent test generated " + (numThreads * idsPerThread) + " IDs in " + durationInSeconds + " seconds.");
    }
}
