package com.SpringProjectsConcepts.ReactiveFileWatcherConcept.BackPressureStrat;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

public class StockPriceLATEST {
    public static void main(String[] args) {
        Flux.create(emitter -> {
                    for (int i = 1; i <= 20; i++) {
                        emitter.next("StockPriceUpdate-" + i);
                        try {
                            Thread.sleep(0);
                        } catch (InterruptedException e) {
                            emitter.error(e);
                            return;
                        }
                    }
                    emitter.complete();
                }, FluxSink.OverflowStrategy.LATEST)
                //.publishOn(Schedulers.boundedElastic())
                .delayElements(Duration.ofMillis(900))
                .doOnNext(System.out::println)
                .blockLast();
    }
}
