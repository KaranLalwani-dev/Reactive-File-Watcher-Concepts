package com.SpringProjectsConcepts.ReactiveFileWatcherConcept.FluxGenerate;

import reactor.core.publisher.Flux;

import java.io.BufferedReader;
import java.io.IOException;

public class FileReader {
    public static void main(String[] args) {
        Flux<String> flieFlux = Flux.generate(
                () -> new BufferedReader(new java.io.FileReader("sample.txt")),
                (reader, sink) -> {
                    try {
                        String line = reader.readLine();
                        if(line!= null) {
                            sink.next(line);
                        } else {
                            sink.complete();
                        }

                    } catch (IOException e) {
                        sink.error(e);
                    }
                    return reader;
                },

                reader ->{
                    try {
                        reader.close();
                        System.out.println("File closed.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
        );

        flieFlux
                .doOnNext(System.out::println)
                .doOnComplete(() -> System.out.println("All lines read."))
                .blockLast();
    }
}
