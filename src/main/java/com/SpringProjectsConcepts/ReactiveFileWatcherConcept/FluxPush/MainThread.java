package com.SpringProjectsConcepts.ReactiveFileWatcherConcept.FluxPush;

import reactor.core.publisher.Flux;

public class MainThread {
    public static void main(String[] args) {
        Flux<Integer> flux = Flux.push(
                sink-> {
                    System.out.println(Thread.currentThread().getName() + "[producer] start");
                    for(int i = 1; i <= 5; i++) {
                        System.out.println(Thread.currentThread().getName() + "[producer] emitted: " + i);
                        sink.next(i);
                    }
                    sink.complete();
                    System.out.println(Thread.currentThread().getName() + "[producer] complete");
                }
        );

        flux
                .doOnNext(i -> System.out.println(Thread.currentThread().getName() + "[consumer] received: " + i))
                .doOnComplete(() -> System.out.println(Thread.currentThread().getName() + "[consumer] complete"))
                .blockLast();
    }
}
