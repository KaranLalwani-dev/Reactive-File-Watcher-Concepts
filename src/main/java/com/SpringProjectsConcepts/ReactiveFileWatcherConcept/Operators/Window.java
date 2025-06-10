package com.SpringProjectsConcepts.ReactiveFileWatcherConcept.Operators;

import reactor.core.publisher.Flux;

public class Window {
    public static void main(String[] args) {
        Flux.range(1, 10)
                .window(3)  // Emit a Flux of 3 items each
                .flatMap(windowFlux ->
                        windowFlux
                                .doOnNext(i -> System.out.print("[" + i + "] "))
                                .then()
                )
                .blockLast();  // Wait for all windows to complete
    }
}
