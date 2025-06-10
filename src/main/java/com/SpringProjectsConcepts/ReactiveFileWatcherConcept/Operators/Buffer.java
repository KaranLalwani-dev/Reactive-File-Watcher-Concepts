package com.SpringProjectsConcepts.ReactiveFileWatcherConcept.Operators;

import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

public class Buffer {
    public static void main(String[] args) throws InterruptedException {
        Flux.interval(Duration.ofMillis(100))  // emit every 100ms
                .take(10)                          // simulate 10 data points
                .map(i -> "SensorData-" + i)       // convert to string
                .buffer(3)                         // buffer in chunks of 3
                .subscribe(Buffer::insertBatchToDB);

        // Give enough time for async execution to finish
        Thread.sleep(2000);
    }

    static void insertBatchToDB(List<String> batch) {
        System.out.println("Inserting batch into DB: " + batch);
    }
}
