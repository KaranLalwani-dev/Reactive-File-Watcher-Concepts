package com.SpringProjectsConcepts.ReactiveFileWatcherConcept.FluxGenerate;

import reactor.core.publisher.Flux;

public class Demo {
    public static void main(String[] args) {
        Flux<Integer> flux = Flux.generate(
                () -> 1,
                (state, sink) -> {
                    sink.next(state);
                    if(state == 5) {
                        sink.complete();
                        return state;
                    }
                    return state + 1;
                }
        );

        flux
                .doOnNext(i -> System.out.println("emitted: " + i))
                .doOnComplete(() -> System.out.println("generation complete"))
                .blockLast();
    }
}
