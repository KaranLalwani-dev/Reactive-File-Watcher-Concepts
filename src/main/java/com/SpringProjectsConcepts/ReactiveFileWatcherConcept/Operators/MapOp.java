package com.SpringProjectsConcepts.ReactiveFileWatcherConcept.Operators;

import reactor.core.publisher.Flux;

public class MapOp {
    public static void main(String[] args) {
        Flux.range(1, 5)
                .map(i -> i * i)  // square each number
                .subscribe(System.out::println);
    }
}
