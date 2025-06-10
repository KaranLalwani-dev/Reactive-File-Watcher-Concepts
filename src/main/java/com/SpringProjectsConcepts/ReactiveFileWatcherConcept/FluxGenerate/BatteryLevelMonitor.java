package com.SpringProjectsConcepts.ReactiveFileWatcherConcept.FluxGenerate;

import reactor.core.publisher.Flux;

public class BatteryLevelMonitor {
    public static void main(String[] args) {
        Flux<Integer> batteryFlux = Flux.generate(
                () -> 100,
                (batteryLevel, sink) -> {
                    sink.next(batteryLevel);

                    if(batteryLevel <= 15) {
                        sink.complete();
                        return batteryLevel;
                    }

                    return  batteryLevel - 15;
                },
                finalLevel -> {
                    System.out.println("Battery monitor shutting down at " + finalLevel + "%");
                }
        );

        batteryFlux
                .doOnNext(level -> System.out.println("Battery at: " + level + "%"))
                .doOnComplete(() -> System.out.println("Monitoring complete"))
                .blockLast();
    }
}
