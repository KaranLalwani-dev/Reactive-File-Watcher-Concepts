package com.SpringProjectsConcepts.ReactiveFileWatcherConcept.FluxCreate;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class ParallelProcessing {
    public static void main(String[] args) {
        Flux<Integer> source = Flux.range(1, 10);

        source
                .parallel(3)
                .runOn(Schedulers.parallel())
                .doOnNext(i -> System.out.println(Thread.currentThread().getName() + "processing " + i))
                .sequential()
                .blockLast();
    }
}
