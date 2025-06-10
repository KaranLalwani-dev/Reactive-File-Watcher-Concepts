package com.SpringProjectsConcepts.ReactiveFileWatcherConcept.BackPressureStrat;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;

public class BUFFERDemo2 {
    public static void main(String[] args) {
        Flux<Integer> flux = Flux.create(sink -> {
            System.out.println(Thread.currentThread().getName() + " started producing");

            for (int i = 1; i <= 20; i++) {
                System.out.println(Thread.currentThread().getName() + " emitted " + i);
                sink.next(i);

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    sink.error(e);
                    return;
                }
            }
            sink.complete();
        }, FluxSink.OverflowStrategy.BUFFER);

        flux
                .publishOn(Schedulers.parallel())
                .doOnNext(item -> {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Thread.currentThread().getName() + " consumed: " + item);
                }).blockLast();
    }
}
