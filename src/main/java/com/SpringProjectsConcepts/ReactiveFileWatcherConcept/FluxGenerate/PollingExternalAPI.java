package com.SpringProjectsConcepts.ReactiveFileWatcherConcept.FluxGenerate;

import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

public class PollingExternalAPI {
    public static void main(String[] args) {
        Flux<Post> postFlux = Flux.generate(
                // 1) Initial state: lastSeenId = 0, pollCount = 0
                () -> new PollState(0L, 0),

                // 2) Generator: poll the API, emit new posts
                (state, sink) -> {
                    // Simulated blocking call: fetch posts since lastSeenId
                    List<Post> newPosts = fetchPostsSince(state.lastSeenId);

                    if (newPosts.isEmpty()) {
                        // If no new posts for 3 consecutive polls, complete
                        if (state.pollCount >= 2) {
                            sink.complete();
                            return state;
                        }
                    } else {
                        // Emit each new post, update lastSeenId
                        long maxId = state.lastSeenId;
                        for (Post p : newPosts) {
                            sink.next(p);
                            maxId = Math.max(maxId, p.id);
                        }
                        state.lastSeenId = maxId;
                    }

                    state.pollCount += 1;
                    return state; // return updated state
                },

                // 3) Cleanup: log shutdown
                finalState -> {
                    System.out.println("Polling completed after " + finalState.pollCount + " polls.");
                }
        );

        // Subscribe with a delay of 5 seconds between polls
        postFlux
                .delayElements(Duration.ofSeconds(5))  // wait 5s between each generator invocation
                .doOnNext(post -> System.out.println("New post: " + post))
                .doOnComplete(() -> System.out.println("No more new posts. Stopping."))
                .blockLast();
    }

    // Simulated blocking REST call
    private static List<Post> fetchPostsSince(long lastSeenId) {
        // Imagine a synchronous HTTP call such as RestTemplate.getForObject(...)
        // For demo, we return random or fixed data
        // ...
        return List.of(); // empty for simplicity
    }

    // Holds state between calls
    static class PollState {
        long lastSeenId;
        int pollCount;
        PollState(long lastSeenId, int pollCount) {
            this.lastSeenId = lastSeenId;
            this.pollCount = pollCount;
        }
    }

    // Simple domain class for a “post”
    static class Post {
        final long id;
        final String text;
        Post(long id, String text) {
            this.id = id; this.text = text;
        }
        @Override
        public String toString() {
            return "[" + id + "] " + text;
        }
    }
}
