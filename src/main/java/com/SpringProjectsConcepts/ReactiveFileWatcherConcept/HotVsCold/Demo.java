package com.SpringProjectsConcepts.ReactiveFileWatcherConcept.HotVsCold;

import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class Demo {
    public static void main(String[] args) throws InterruptedException {
        Flux<Integer> coldFlux = Flux.range(1, 5)
                .delayElements(Duration.ofMillis(200))
                .doOnSubscribe(s -> System.out.println("Cold new subscription"))
                .doOnNext(i -> System.out.println("[Cold] Emitted: " + i));

        coldFlux.subscribe(i -> System.out.println("Subscriber(A) Cold received: " + i));
        Thread.sleep(500);

        // Subscriber B on same Cold (gets full sequence again)
        coldFlux.subscribe(i -> System.out.println("Subscriber B (cold) received: " + i));

        Thread.sleep(2000);

        System.out.println("\n--- Now converting to Hot ---\n");

        // 2. Convert Cold to Hot using publish().autoConnect(1)
        Flux<Integer> hotFlux = coldFlux
                .doOnNext(i -> System.out.println("[Hot] Emitted: " + i))
                .publish()
                .autoConnect(1); // hot stream starts on first subscriber

        // Subscriber A on Hot
        hotFlux.subscribe(i -> System.out.println("Subscriber A (hot) received: " + i));

        // Delay before Subscriber B
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Subscriber B on Hot (missed initial elements)
        hotFlux.subscribe(i -> System.out.println("Subscriber B (hot) received: " + i));

        // Keep JVM alive until completion
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
