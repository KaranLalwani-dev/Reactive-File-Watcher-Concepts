package com.SpringProjectsConcepts.ReactiveFileWatcherConcept.FileWatcher;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import static java.nio.file.StandardWatchEventKinds.*;

public class FileWatcher_2 {
    private final WatchService watchService;
    private final Map<WatchKey, Path> keyPathMap = new HashMap<>();
    private final Map<Path, List<String>> fileSnapshots = new HashMap<>();
    private final Map<Path, Long> lastModifyTime = new HashMap<>();
    private final long debounceMillis = 300;

    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public FileWatcher_2(Path startDir) throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
        registerAll(startDir);
    }

    private void registerAll(final Path start) throws IOException {
        Files.walk(start)
                .filter(Files::isDirectory)
                .forEach(dir -> {
                    try {
                        WatchKey key = dir.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
                        keyPathMap.put(key, dir);
                    } catch (IOException e) {
                        System.err.println("Failed to register: " + dir);
                    }
                });
    }

    public void processEvents() {
        System.out.println("\u001B[36mStarting DiffRecursiveFileWatcher\u001B[0m");
        while (true) {
            WatchKey key;
            try {
                key = watchService.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            Path dir = keyPathMap.get(key);
            if (dir == null) continue;

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                if (kind == OVERFLOW) continue;

                Path name = ((WatchEvent<Path>) event).context();

                String filename = name.toString();
                if (filename.endsWith("~")) {
                    continue; // skip all events for these files
                }

                Path fullPath = dir.resolve(name);
                String now = LocalDateTime.now().format(fmt);

                // Debounce MODIFY
                if (kind == ENTRY_MODIFY) {
                    long last = lastModifyTime.getOrDefault(fullPath, 0L);
                    long diff = System.currentTimeMillis() - last;
                    if (diff < debounceMillis) continue;
                    lastModifyTime.put(fullPath, System.currentTimeMillis());
                }

                // Colorize
                String color = kind == ENTRY_CREATE  ? "\u001B[32m" :
                        kind == ENTRY_DELETE  ? "\u001B[31m" :
                                "\u001B[33m";
                System.out.printf("%s[%s] %-8s %s\u001B[0m%n",
                        color, now, kind.name(), fullPath);

                // On MODIFICATION of .txt or .java: show diff
                if (kind == ENTRY_MODIFY &&
                        (fullPath.toString().endsWith(".txt") || fullPath.toString().endsWith(".java"))
                ) {
                    diffFile(fullPath);
                }

                // If new directory created, register it
                if (kind == ENTRY_CREATE && Files.isDirectory(fullPath)) {
                    try {
                        registerAll(fullPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            boolean valid = key.reset();
            if (!valid) keyPathMap.remove(key);
            if (keyPathMap.isEmpty()) break;
        }
    }

    private void diffFile(Path path) {
        try {
            List<String> oldLines = fileSnapshots.getOrDefault(path, Collections.emptyList());
            List<String> newLines = Files.readAllLines(path);

            int max = Math.max(oldLines.size(), newLines.size());
            for (int i = 0; i < max; i++) {
                String oldLine = i < oldLines.size() ? oldLines.get(i) : "";
                String newLine = i < newLines.size() ? newLines.get(i) : "";
                if (!oldLine.equals(newLine)) {
                    System.out.printf("    \u001B[34mLine %d:\u001B[0m\n      \u001B[31m- %s\u001B[0m\n      \u001B[32m+ %s\u001B[0m%n",
                            i+1, oldLine, newLine);
                }
            }
            fileSnapshots.put(path, newLines);
        } catch (IOException e) {
            System.err.println("Error diffing file " + path + ": " + e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        Path pathToWatch = Paths.get(
                System.getProperty("user.dir"),
                "src", "main", "java", "com", "SpringProjectsConcepts", "ReactiveFileWatcherConcept"
        );
        FileWatcher_2 watcher = new FileWatcher_2(pathToWatch);
        watcher.processEvents();
    }
}
