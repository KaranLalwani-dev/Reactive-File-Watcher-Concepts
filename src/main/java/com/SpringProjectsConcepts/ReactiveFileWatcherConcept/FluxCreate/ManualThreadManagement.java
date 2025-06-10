package com.SpringProjectsConcepts.ReactiveFileWatcherConcept.FluxCreate;

import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

public class ManualThreadManagement {
    public static void main(String[] args) {

        Flux<String> flux = Flux.create(sink -> {

            Thread producerA = new Thread(() -> {
                for (int i = 1; i <= 5; i++) {
                    sink.next("A-" + i);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        sink.error(e);
                        return;
                    }
                }
            }, "Producer-A");

            Thread producerB = new Thread(() -> {
                for (int i = 1; i <= 5; i++) {
                    sink.next("B-" + i);
                    try {
                        Thread.sleep(150);
                    } catch (InterruptedException e) {
                        sink.error(e);
                        return;
                    }
                }
                sink.complete();
            }, "Producer-B");

            sink.onCancel(() -> {
                producerA.interrupt();
                producerB.interrupt();
            });

            producerA.start();
            producerB.start();
        }, FluxSink.OverflowStrategy.BUFFER);

        flux.subscribe(new BaseSubscriber<String>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(1);
            }

            @Override
            protected void hookOnNext(String value) {
                System.out.println(Thread.currentThread().getName() + " received: " + value);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                request(1);
            }

            @Override
            protected void hookOnComplete() {
                System.out.println(Thread.currentThread().getName() + " completed.");
            }
        });

        // Give producers and subscriber enough time to finish
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ignored) {
        }
    }
}
