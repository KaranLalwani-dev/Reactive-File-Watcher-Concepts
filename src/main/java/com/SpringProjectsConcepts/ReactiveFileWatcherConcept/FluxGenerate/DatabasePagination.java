package com.SpringProjectsConcepts.ReactiveFileWatcherConcept.FluxGenerate;

import reactor.core.publisher.Flux;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabasePagination {
    public static void main(String[] args) {
        Flux<MyEntity> pageFlux = Flux.generate(
                // 1) Initial state: start at page 0
                () -> 0,

                // 2) Generator: fetch one page at a time, emit each row
                (pageIndex, sink) -> {
                    List<MyEntity> rows = fetchPageFromDb(pageIndex, 100);
                    if (rows.isEmpty()) {
                        // No more data: complete the Flux
                        sink.complete();
                        return pageIndex; // state won’t be used again
                    }
                    // Emit each row of this page
                    for (MyEntity e : rows) {
                        sink.next(e);
                    }
                    // Move on to the next page
                    return pageIndex + 1;
                },

                // 3) Cleanup: (optional) close any shared resources if needed
                finalPage -> {
                    System.out.println("Finished streaming all pages. Last page = " + finalPage);
                }
        );

        // Subscribe (for demo) and print each entity
        pageFlux
                .doOnNext(entity -> System.out.println("Got row: " + entity))
                .doOnComplete(() -> System.out.println("All rows streamed."))
                .blockLast();
    }

    // Simulated blocking JDBC call that returns a page of 100 rows
    private static List<MyEntity> fetchPageFromDb(int pageIndex, int pageSize) {
        List<MyEntity> results = new ArrayList<>();
        // (1) Set up JDBC connection, prepare statement with OFFSET, LIMIT
        try (Connection conn = DriverManager.getConnection("jdbc:your-db-url", "user", "pass");
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT id, name, value FROM my_table ORDER BY id LIMIT ? OFFSET ?")) {
            ps.setInt(1, pageSize);
            ps.setInt(2, pageIndex * pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MyEntity e = new MyEntity(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getDouble("value")
                    );
                    results.add(e);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return results;
    }

    // Simple domain class
    static class MyEntity {
        final long id;
        final String name;
        final double value;
        MyEntity(long id, String name, double value) {
            this.id = id; this.name = name; this.value = value;
        }
        @Override
        public String toString() {
            return "MyEntity[id=" + id + ", name=" + name + ", value=" + value + "]";
        }
    }
}
