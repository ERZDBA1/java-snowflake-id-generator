package com.hmwcs.snowflake.generator;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Specify order strategy
public class SnowflakeIdGeneratorTest {
    private SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(9, 29);
    private final int numThreads = Runtime.getRuntime().availableProcessors();
    private int idsPerThread = 1_000_000;

    @Test
    @Order(0)
    void idGenerationTest() {
        long uniqueId = idGenerator.nextId();
        System.out.println("Generated Snowflake ID: " + uniqueId);
        System.out.println("In Binary Form: " + Long.toBinaryString(uniqueId));
    }

    @Test
    @Order(1)
    void benchmarkTest() {
        int iterations = numThreads * idsPerThread; // Total number of IDs to generate in the benchmark test
        Set<Long> uniqueIds = new HashSet<>();

        long startTime = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            long id = idGenerator.nextId();
            if (!uniqueIds.add(id)) fail("Duplicate ID detected: " + id);
        }
        long endTime = System.nanoTime();
        double durationInSeconds = (endTime - startTime) / 1_000_000_000.0; // Convert nanoseconds to seconds

        System.out.println("Generated " + iterations + " unique IDs in " + durationInSeconds + " seconds.");
    }

    @Test
    @Order(2)
    void concurrentBenchmarkTest() {
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        Set<Long> uniqueIds = ConcurrentHashMap.newKeySet();

        long startTime = System.nanoTime();

        for (int i = 0; i < numThreads; i++)
            executor.submit(() -> {
                for (int j = 0; j < idsPerThread; j++) {
                    long id = idGenerator.nextId();
                    if (!uniqueIds.add(id)) fail("Duplicate ID detected: " + id);
                }
            });

        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.HOURS); // Wait for all tasks to complete or timeout after 1 hour
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Concurrent test interrupted.");
        }

        long endTime = System.nanoTime();
        double durationInSeconds = (endTime - startTime) / 1_000_000_000.0; // Convert nanoseconds to seconds

        System.out.println("Generated " + (numThreads * idsPerThread) + " unique IDs concurrently in " + durationInSeconds + " seconds.");
    }
}
