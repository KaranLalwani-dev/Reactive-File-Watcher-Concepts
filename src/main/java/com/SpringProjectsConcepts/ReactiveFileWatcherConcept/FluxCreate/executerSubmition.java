package com.SpringProjectsConcepts.ReactiveFileWatcherConcept.FluxCreate;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class executerSubmition {
    public static void main(String[] args) throws InterruptedException {
        // Create a bounded Elastic thread pool to simulate multiple threads emitting
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Flux<Integer> flux = Flux.create(sink -> {
                    // Submit two tasks that both emit numbers
                    executor.submit(() -> {
                        for (int i = 1; i <= 3; i++) {
                            System.out.println("Thread A emitting: " + i);
                            sink.next(i);
                            sleep(150);
                        }
                    });
                    executor.submit(() -> {
                        for (int i = 4; i <= 5; i++) {
                            System.out.println("Thread B emitting: " + i);
                            sink.next(i);
                            sleep(150);
                        }
                    });
                    // After both tasks, complete
                    executor.submit(() -> {
                        sleep(500);
                        sink.complete();
                    });
                }, FluxSink.OverflowStrategy.BUFFER // choose a backpressure strategy
        );

        flux
                .doOnNext(i -> System.out.println("create received: " + i))
                .doOnComplete(() -> {
                    System.out.println("create completed");
                    executor.shutdown();
                })
                .blockLast();
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {}
    }
}
