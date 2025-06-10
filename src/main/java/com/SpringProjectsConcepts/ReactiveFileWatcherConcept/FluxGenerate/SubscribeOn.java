package com.SpringProjectsConcepts.ReactiveFileWatcherConcept.FluxGenerate;

import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;
import reactor.core.scheduler.Schedulers;

public class SubscribeOn {
    public static void main(String[] args) {

        Flux<Integer> flux = Flux.generate(
                        () -> 1, // initial state
                        (Integer state, SynchronousSink<Integer> sink) -> {
                            System.out.println(Thread.currentThread().getName() + " emitting: " + state);
                            sink.next(state);
                            if (state == 5) {
                                sink.complete();
                                return state;
                            }
                            return state + 1;
                        }
                )
                // Shift execution of generator to worker thread
                .subscribeOn(Schedulers.parallel());

        flux.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                System.out.println(Thread.currentThread().getName() + " Subscribed, requesting 1...");
                request(1);
            }

            @Override
            protected void hookOnNext(Integer value) {
                System.out.println(Thread.currentThread().getName() + " Received (onNext): " + value);
                request(1);
            }

            @Override
            protected void hookOnComplete() {
                System.out.println(Thread.currentThread().getName() + " Stream completed (hookOnComplete)");
            }

            @Override
            protected void hookOnError(Throwable throwable) {
                System.out.println("Error encountered: " + throwable.getMessage());
            }
        });

        // Prevent the JVM from exiting before async work completes
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {}
    }
}
