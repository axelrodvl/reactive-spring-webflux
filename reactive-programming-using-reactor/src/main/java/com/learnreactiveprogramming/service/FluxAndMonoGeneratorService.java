package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class FluxAndMonoGeneratorService {
    public Flux<String> namesFlux() {
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .log(); // db or a remove service call
    }

    public Mono<String> namesMono() {
        return Mono.just("alex")
                .log();
    }

    public Flux<String> namesFlux_map(int stringLength) {
        // filter the string whose length is greater than 3
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength) // 4-ALEX, 5-CHLOE
                .map(s -> s.length() + " - " + s)
                .log(); // db or a remove service call
    }

    public Flux<String> namesFlux_immutability() {
        var namesFlux = Flux.fromIterable(List.of("alex", "ben", "chloe"));

        namesFlux.map(String::toUpperCase);

        return namesFlux;
    }

    public Mono<String> namesMono_map_filter(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength);
    }

    public Mono<List<String>> namesMono_flatMap(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(this::splitStringMono)
                .log(); // Mono<List of A, L, E, X>
    }

    public Flux<String> namesMono_flatMapMany(int stringLength) {
        return Mono.just("alex")
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMapMany(this::splitString)
                .log();
    }

    private Mono<List<String>> splitStringMono(String s) {
        var charArray = s.split("");
        var charList = List.of(charArray); // ALEX -> A, L, E, X
        return Mono.just(charList);
    }

    public Flux<String> namesFlux_flatMap(int stringLength) {
        // filter the string whose length is greater than 3
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength) // 4-ALEX, 5-CHLOE
                // A L E X C H L O E
                .flatMap(s -> splitString(s)) // A L E X C H L O E
                .log(); // db or a remove service call
    }

    public Flux<String> namesFlux_flatMap_async(int stringLength) {
        // filter the string whose length is greater than 3
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength) // 4-ALEX, 5-CHLOE
                // A L E X C H L O E
                .flatMap(s -> splitString_withDelay(s)) // A L E X C H L O E
                .log(); // db or a remove service call
    }

    public Flux<String> namesFlux_concatMap(int stringLength) {
        // filter the string whose length is greater than 3
        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength) // 4-ALEX, 5-CHLOE
                // A L E X C H L O E
                .concatMap(s -> splitString_withDelay(s)) // A L E X C H L O E
                .log(); // db or a remove service call
    }

    public Flux<String> namesFlux_transform(int stringLength) {
        // filter the string whose length is greater than 3

        Function<Flux<String>, Flux<String>> filterMap
                = name -> name
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength);

        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .transform(filterMap)
                .flatMap(s -> splitString(s)) // A L E X C H L O E
                .defaultIfEmpty("default")
                .log(); // db or a remove service call
    }

    public Flux<String> namesFlux_transform_switchIfEmpty(int stringLength) {
        // filter the string whose length is greater than 3

        Function<Flux<String>, Flux<String>> filterMap
                = name -> name
                .map(String::toUpperCase)
                .filter(s -> s.length() > stringLength)
                .flatMap(s -> splitString(s));

        var defaultFlux = Flux.just("default")
                .transform(filterMap); // "D", "E", "F", "A", "U", "L", "T"

        return Flux.fromIterable(List.of("alex", "ben", "chloe"))
                .transform(filterMap)
                 // A L E X C H L O E
                .switchIfEmpty(defaultFlux)
                .log(); // db or a remove service call
    }

    public Flux<String> explore_concat() {
        var abcFlux = Flux.just("A", "B", "C").log();

        var defFlux = Flux.just("D", "E", "F").log();

        return Flux.concat(abcFlux, defFlux)
                .log();
    }

    public Flux<String> explore_concatWith() {
        var abcFlux = Flux.just("A", "B", "C").log();

        var defFlux = Flux.just("D", "E", "F").log();

        return abcFlux.concatWith(defFlux).log();
    }

    public Flux<String> explore_concatWith_mono() {
        var aMono = Mono.just("A").log();

        var bMono = Flux.just("B").log();

        return aMono.concatWith(bMono).log();
    }

    public Flux<String> explore_merge() {
        var abcFlux = Flux.just("A", "B", "C") // A,B
                .delayElements(Duration.ofMillis(100))
                .log();

        var defFlux = Flux.just("D", "E", "F") // D,E
                .delayElements(Duration.ofMillis(125))
                .log();

        return Flux.merge(abcFlux, defFlux)
                .log();
    }

    public Flux<String> explore_mergeWith() {
        var abcFlux = Flux.just("A", "B", "C") // A,B
                .delayElements(Duration.ofMillis(100))
                .log();

        var defFlux = Flux.just("D", "E", "F") // D,E
                .delayElements(Duration.ofMillis(125))
                .log();

        return abcFlux.mergeWith(defFlux)
                .log();
    }

    public Flux<String> explore_mergeWith_mono() {
        var aMono = Mono.just("A") // A
                .log();

        var bMono = Mono.just("B") // B
                .log();

        return aMono.mergeWith(bMono)
                .log();
    }

    public Flux<String> explore_mergeSequential() {
        var abcFlux = Flux.just("A", "B", "C") // A,B
                .delayElements(Duration.ofMillis(100))
                .log();

        var defFlux = Flux.just("D", "E", "F") // D,E
                .delayElements(Duration.ofMillis(125))
                .log();

        return Flux.mergeSequential(abcFlux, defFlux)
                .log();
    }

    public Flux<String> explore_zip() {
        var abcFlux = Flux.just("A", "B", "C") // A,B
                .log();

        var defFlux = Flux.just("D", "E", "F") // D,E
                .log();

        return Flux.zip(abcFlux, defFlux, (first, second) -> first + second)
                .log();
    }

    public Flux<String> explore_zip_1() {
        var abcFlux = Flux.just("A", "B", "C") // A,B
                .log();

        var defFlux = Flux.just("D", "E", "F") // D,E
                .log();

        var _123Flux = Flux.just("1", "2", "3")
                .log();

        var _456Flux = Flux.just("4", "5", "6")
                .log();

        return Flux.zip(abcFlux, defFlux, _123Flux, _456Flux)
                .map(t4 -> t4.getT1() + t4.getT2() + t4.getT3() + t4.getT4())
                .log();
    }

    public Flux<String> explore_zipWith() {
        var abcFlux = Flux.just("A", "B", "C") // A,B
                .log();

        var defFlux = Flux.just("D", "E", "F") // D,E
                .log();

        return abcFlux.zipWith(defFlux, (first, second) -> first + second)
                .log(); // AD, BE, CF
    }

    public Mono<String> explore_zipWith_mono() {
        var aMono = Mono.just("A") // A
                .log();

        var bMono = Mono.just("B") // B
                .log();

        return aMono.zipWith(bMono)
                .map(t2 -> t2.getT1() + t2.getT2()) // AB
                .log();
    }

    public Flux<String> splitString(String name) {
        var charArray = name.split("");
        return Flux.fromArray(charArray);
    }

    public Flux<String> splitString_withDelay(String name) {
        var charArray = name.split("");
//        var delay = new Random().nextInt(1000);
        return Flux.fromArray(charArray)
                .delayElements(Duration.ofMillis(1000));
    }

    public static void main(String[] args) {
        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();
        fluxAndMonoGeneratorService.namesFlux()
                .subscribe(name -> {
                    System.out.println("Name is " + name);
                });

        fluxAndMonoGeneratorService.namesMono()
                .subscribe(name -> {
                    System.out.println("Mono name is " + name);
                });
    }
}
