package com.SpringProjectsConcepts.ReactiveFileWatcherConcept.Operators;

import reactor.core.publisher.Flux;

import java.time.Duration;

public class WindowTimeOut {
    public static void main(String[] args) throws InterruptedException {
        Flux.interval(Duration.ofMillis(300))
                .windowTimeout(4, Duration.ofSeconds(1))
                .flatMap(window -> window.collectList())
                .subscribe(list -> System.out.println("Window: " + list));

        Thread.sleep(4000);  // Keep JVM alive to see output
    }
}
