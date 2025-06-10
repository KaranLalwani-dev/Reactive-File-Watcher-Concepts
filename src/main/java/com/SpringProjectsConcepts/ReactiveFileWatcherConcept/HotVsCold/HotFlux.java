package com.SpringProjectsConcepts.ReactiveFileWatcherConcept.HotVsCold;

import reactor.core.publisher.Flux;

import java.time.Duration;

public class HotFlux {
    public static void main(String[] args) throws InterruptedException {
        Flux<Integer> hotFlux = Flux.range(1, 10)
                .delayElements(Duration.ofMillis(200))
                .doOnSubscribe(s -> System.out.println("Hot new subscription"))
                .doOnNext(i -> System.out.println("[Hot] Emitted: " + i))
                .publish()
                .autoConnect(1);

        hotFlux.subscribe(i -> System.out.println("Subscriber A hot received: " + i));

        Thread.sleep(500);

        hotFlux.subscribe(i -> System.out.println("Subscriber B hot received: " + i));

        Thread.sleep(2000);

    }
}
