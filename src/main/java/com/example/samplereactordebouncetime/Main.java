package com.example.samplereactordebouncetime;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        final Flux<Object> typings = Flux.create(emitter -> {
            try {
                emitter.next("j");
                Thread.sleep(10);
                emitter.next("ja");
                Thread.sleep(150);
                emitter.next("jav");
                Thread.sleep(50);
                emitter.next("java");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        final WebClient webClient = WebClient.create("http://localhost:8080/api/search");

        typings
                .sampleTimeout(o -> Mono.empty().delaySubscription(Duration.ofMillis(100)))
                .concatMap(o -> webClient
                        .get()
                        .uri(uriBuilder -> uriBuilder.queryParam("text", o).build())
                        .retrieve()
                        .bodyToMono(String[].class)
                )
                .subscribe(
                        data -> System.out.println(Arrays.asList(data)),
                        error -> System.out.println(error.getMessage()),
                        () -> System.out.println("Complete")
                );

        typings.blockLast();
    }

}
