package com.SpringProjectsConcepts.ReactiveFileWatcherConcept.Operators;

import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

public class TimeBasedBuffer {
    public static void main(String[] args) throws InterruptedException {
        Flux.interval(Duration.ofMillis(200))       // emit an item every 200ms
                .map(i -> "Log-" + i)
                .buffer(Duration.ofSeconds(1))          // collect items every 1 second
                .subscribe(TimeBasedBuffer::writeToDisk);

        // Wait to allow buffer emissions
        Thread.sleep(4000);
    }

    static void writeToDisk(List<String> logs) {
        System.out.println("Writing to disk: " + logs);
    }
}
