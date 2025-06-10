package com.SpringProjectsConcepts.ReactiveFileWatcherConcept.Operators;

import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

public class BufferTimedOut {
    public static void main(String[] args) throws InterruptedException {
        Flux.interval(Duration.ofMillis(400))         // Emit every 400ms (~2.5 items per sec)
                .map(i -> "Log-" + i)
                .bufferTimeout(5, Duration.ofSeconds(2))  // Emit every 5 items OR every 2s
                .subscribe(BufferTimedOut::process);

        Thread.sleep(8000); // Let it run long enough
    }

    static void process(List<String> logs) {
        System.out.println("Batch saved: " + logs);
    }
}
