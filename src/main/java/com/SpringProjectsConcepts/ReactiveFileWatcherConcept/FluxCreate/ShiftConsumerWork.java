package com.SpringProjectsConcepts.ReactiveFileWatcherConcept.FluxCreate;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class ShiftConsumerWork {
    public static void main(String[] args) {
        Flux<String> flux = Flux.create(sink-> {
            System.out.println(Thread.currentThread().getName() + "started emitting..");

            for (int i = 1; i <= 5; i++) {
                sink.next(Thread.currentThread().getName() + "emitted-" + i);

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    sink.error(e);
                    return;
                }
            }
            sink.complete();
            System.out.println(Thread.currentThread().getName() + "finished emitting.");
        });

        flux
                .publishOn(Schedulers.parallel())
                .doOnNext(item -> System.out.println(
                        Thread.currentThread().getName() + " received: " + item))
                .doOnComplete(() -> System.out.println(
                        Thread.currentThread().getName() + " completed"))
                .blockLast(); // Block until completion to keep main alive
    }
}
