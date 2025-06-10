package com.SpringProjectsConcepts.ReactiveFileWatcherConcept.FileWatcher;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.*;

public class FileWatcher_1 {

    private final WatchService watchService;
    private final Map<WatchKey, Path> keyPathMap = new HashMap<>();

    public FileWatcher_1(Path startDir) throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
        registerAll(startDir);
    }


    private void registerAll(final Path start) throws IOException {
        Files.walk(start)
                .filter(Files::isDirectory)
                .forEach(path -> {
                    try {
                        WatchKey key = path.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
                        keyPathMap.put(key, path);
                        System.out.println("Watching: " + path);
                    } catch (IOException e) {
                        System.err.println("Failed to register: " + path);
                        e.printStackTrace();
                    }
                });
    }

    public void processEvents() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        while (true) {
            WatchKey key;
            try {
                key = watchService.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            Path dir = keyPathMap.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!");
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();


                Path name = (Path) event.context();
                if (name.toString().endsWith("~")) {
                    continue; // ignore this event
                }
                Path child = dir.resolve(name);

                String timestamp = LocalDateTime.now().format(formatter);

                System.out.printf("[%s] %s : %s%n", timestamp, kind.name(), child);

                if (kind == ENTRY_CREATE) {
                    try {
                        if (Files.isDirectory(child)) {
                            registerAll(child);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            boolean valid = key.reset();
            if (!valid) {
                keyPathMap.remove(key);
                if (keyPathMap.isEmpty()) break;
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Path pathToWatch = Paths.get(
                System.getProperty("user.dir"),
                "src", "main", "java", "com", "SpringProjectsConcepts", "ReactiveFileWatcherConcept"
        );

        System.out.println("Monitoring: " + pathToWatch);

        FileWatcher_1 watcher = new FileWatcher_1(pathToWatch);
        watcher.processEvents();
    }
}
