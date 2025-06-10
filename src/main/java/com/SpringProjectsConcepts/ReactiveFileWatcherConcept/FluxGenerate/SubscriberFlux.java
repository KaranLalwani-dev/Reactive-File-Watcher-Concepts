package com.SpringProjectsConcepts.ReactiveFileWatcherConcept.FluxGenerate;

import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

public class SubscriberFlux {
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

        flux.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                System.out.println("Subscribed, requesting 1 element...");
                request(1);
            }

            @Override
            protected void hookOnNext(Integer value) {
                System.out.println("Received (onNext): " + value);

                System.out.println("Requesting 1 more...");
                request(1);
            }

            @Override
            protected void hookOnComplete() {
                System.out.println("Stream completed (hookOnComplete)");
            }

            @Override
            protected void hookOnError(Throwable throwable) {
                System.out.println("Error encountered: " + throwable.getMessage());
            }
        });

        try {
            Thread.sleep(2000); // no use of this here all work is done by the main thread itself.
        } catch (InterruptedException ignored) {}

    }
}
