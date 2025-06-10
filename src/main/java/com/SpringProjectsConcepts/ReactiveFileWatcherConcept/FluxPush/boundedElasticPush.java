package com.SpringProjectsConcepts.ReactiveFileWatcherConcept.FluxPush;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import static reactor.core.publisher.Flux.push;

public class boundedElasticPush {
    public static void main(String[] args) {
        Flux<Integer> flux = Flux.<Integer>push(
                   sink -> {
                    System.out.println(Thread.currentThread().getName() + "[producer] start");
                    for (int i = 1; i <= 5; i++) {
                        System.out.println(Thread.currentThread().getName() + "[producer] emitted: " + i);
                        sink.next(i);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            sink.error(e);
                            return;
                        }
                    }

                    sink.complete();
                    System.out.println(Thread.currentThread().getName() + "[producer] complete");
                }
        ).subscribeOn(Schedulers.boundedElastic());
    }
}
