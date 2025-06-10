package com.SpringProjectsConcepts.ReactiveFileWatcherConcept.Operators;

import reactor.core.publisher.Flux;

public class FilterMap {
    public static void main(String[] args) {
        Flux.range(1, 10)
                .filter(i -> i % 2 == 0)  // keep even numbers
                .subscribe(System.out::println);
    }
}
