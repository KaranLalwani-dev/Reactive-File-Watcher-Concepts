package com.SpringProjectsConcepts.ReactiveFileWatcherConcept.FileWatcher;

import java.io.IOException;
import java.nio.file.*;

public class FileWatcher_0 {
    public static void main (String[] args) throws IOException, InterruptedException {
        WatchService watcher = FileSystems.getDefault().newWatchService();

        Path path = Paths.get(System.getProperty("user.home"));

        path.register(
                watcher, StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);

        WatchKey key;
        while((key = watcher.take()) != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
                System.out.println(
                        "Event kind: " + event.kind()
                        + ". File affected: " + event.context() + ".");

            }
            key.reset();
        }
    }
}
