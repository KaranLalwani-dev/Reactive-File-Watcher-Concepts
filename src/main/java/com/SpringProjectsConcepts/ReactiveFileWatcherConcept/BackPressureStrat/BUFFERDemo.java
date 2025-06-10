package com.SpringProjectsConcepts.ReactiveFileWatcherConcept.BackPressureStrat;

import reactor.core.publisher.Flux;

import java.time.Duration;

public class BUFFERDemo {
    public static void main(String[] args) {
        Flux<Integer> fastProducer = Flux.range(1, 10)
                .delayElements(Duration.ofMillis(50))
                        .doOnNext(i -> System.out.println(
                                Thread.currentThread().getName() + " emitted " + i
                        ));

        fastProducer
                .onBackpressureBuffer()
                .delayElements(Duration.ofMillis(200))
                .doOnNext(i -> System.out.println(
                        Thread.currentThread().getName() + " consumed: " + i))
                .blockLast();

    }
}
