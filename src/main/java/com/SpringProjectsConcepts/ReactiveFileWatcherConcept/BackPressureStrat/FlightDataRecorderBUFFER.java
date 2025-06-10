package com.SpringProjectsConcepts.ReactiveFileWatcherConcept.BackPressureStrat;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;

import static java.lang.Thread.sleep;

public class FlightDataRecorderBUFFER {
    public static void main(String[] args) {
        System.out.println("\n--- BUFFER Strategy (Flight Data Recorder) ---");
        Flux.create(sink -> {
                    for (int i = 1; i <= 20; i++) {
                        sink.next("Telemetry-" + i);
                        try {
                            sleep(50); // Fast producer
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    sink.complete();
                }, FluxSink.OverflowStrategy.BUFFER)
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(data -> {
                    try {
                        sleep(200); // Slow consumer
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("BUFFER Consumed: " + data);
                })
                .subscribe();
    }
}
