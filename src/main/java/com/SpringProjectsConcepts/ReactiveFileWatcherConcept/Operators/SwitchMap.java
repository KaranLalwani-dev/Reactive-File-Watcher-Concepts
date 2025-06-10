package com.SpringProjectsConcepts.ReactiveFileWatcherConcept.Operators;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class SwitchMap {
    public static void main(String[] args) throws InterruptedException {
        Flux.range(1, 3)
                .switchMap(i ->
                        Mono.just(Thread.currentThread().getName() + " Value " + i)
                                .delayElement(Duration.ofMillis(100 * (4 - i)))
                )
                .subscribe(System.out::println);
        Thread.sleep(1000);
    }
}
