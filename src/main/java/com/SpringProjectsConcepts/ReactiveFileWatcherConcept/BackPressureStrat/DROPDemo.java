package com.SpringProjectsConcepts.ReactiveFileWatcherConcept.BackPressureStrat;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;

public class DROPDemo {
    public static void main(String[] args) {
        Flux<Integer> flux = Flux.create(sink -> {
            for (int i = 1; i <= 20; i++) {
                System.out.println("Emitting: " + i);
                sink.next(i);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    sink.error(e);
                }
            }
            sink.complete();
        }, FluxSink.OverflowStrategy.DROP);

        flux
                .publishOn(Schedulers.parallel(), 1) // small buffer size
                .doOnNext(i -> {
                    try {
                        Thread.sleep(200); // simulate slow subscriber
                    } catch (InterruptedException e) {}
                    System.out.println("Consumed: " + i);
                })
                .blockLast();

    }
}
