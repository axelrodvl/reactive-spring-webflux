package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FluxAndMonoGeneratorServiceTest {
    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Test
    void namesFlux() {
        // given

        // when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux();

        // then
        StepVerifier.create(namesFlux)
//                .expectNext("alex", "ben", "chloe")
//                .expectNextCount(3)
                .expectNext("alex")
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void namesFlux_map() {
        // given
        int stringLength = 3;

        // when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_map(stringLength);

        // then
        StepVerifier.create(namesFlux)
                .expectNext("4 - ALEX", "5 - CHLOE")
                .verifyComplete();
    }

    @Test
    void namesFlux_immutability() {

        // when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_immutability();

        // then
        StepVerifier.create(namesFlux)
                .expectNext("alex", "ben", "chloe")
                .verifyComplete();
    }

    @Test
    void namesFlux_flatMap() {
        // given
        int stringLength = 3;

        // when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_flatMap(stringLength);

        // then
        StepVerifier.create(namesFlux)
                .expectNext("A","L","E","X","C","H","L","O","E")
                .verifyComplete();
    }

    @Test
    void namesFlux_flatMap_async() {
        // given
        int stringLength = 3;

        // when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_flatMap_async(stringLength);

        // then
        StepVerifier.create(namesFlux)
//                .expectNext("A","L","E","X","C","H","L","O","E")
                .expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void namesFlux_concatMap() {
        // given
        int stringLength = 3;

        // when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_concatMap(stringLength);

        // then
        StepVerifier.create(namesFlux)
                .expectNext("A","L","E","X","C","H","L","O","E")
//                .expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void namesMono_flatMap() {
        // given
        int stringLength = 3;

        // when
        var value = fluxAndMonoGeneratorService.namesMono_flatMap(stringLength);

        // then
        StepVerifier.create(value)
                .expectNext(List.of("A", "L", "E", "X"))
                .verifyComplete();
    }

    @Test
    void namesMono_flatMapMany() {
        // given
        int stringLength = 3;

        // when
        var value = fluxAndMonoGeneratorService.namesMono_flatMapMany(stringLength);

        // then
        StepVerifier.create(value)
                .expectNext("A", "L", "E", "X")
                .verifyComplete();
    }

    @Test
    void namesFlux_transform() {
        // given
        int stringLength = 3;

        // when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_transform(stringLength);

        // then
        StepVerifier.create(namesFlux)
                .expectNext("A","L","E","X","C","H","L","O","E")
                .verifyComplete();
    }

    @Test
    void namesFlux_transform_1() {
        // given
        int stringLength = 6;

        // when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_transform(stringLength);

        // then
        StepVerifier.create(namesFlux)
//                .expectNext("A","L","E","X","C","H","L","O","E")
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    void namesFlux_transform_switchIfEmpty() {
        // given
        int stringLength = 6;

        // when
        var namesFlux = fluxAndMonoGeneratorService.namesFlux_transform_switchIfEmpty(stringLength);

        // then
        StepVerifier.create(namesFlux)
//                .expectNext("A","L","E","X","C","H","L","O","E")
                .expectNext("D", "E", "F", "A", "U", "L", "T")
                .verifyComplete();
    }

    @Test
    void explore_concat() {
        // given

        // when
        var concatFlux = fluxAndMonoGeneratorService.explore_concat();

        // then
        StepVerifier.create(concatFlux)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void explore_concatWith() {
        // given

        // when
        var concatWithFlux = fluxAndMonoGeneratorService.explore_concatWith();

        // then
        StepVerifier.create(concatWithFlux)
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void explore_concatWith_mono() {
        // given

        // when
        var concatWithMonoFlux = fluxAndMonoGeneratorService.explore_concatWith_mono();

        // then
        StepVerifier.create(concatWithMonoFlux)
                .expectNext("A", "B")
                .verifyComplete();
    }

    @Test
    void explore_merge() {
        // given

        // when
        var exploreMergeFlux = fluxAndMonoGeneratorService.explore_merge();

        // then
        StepVerifier.create(exploreMergeFlux)
                .expectNext("A", "D", "B", "E", "C", "F") // 457 ms
                .verifyComplete();
    }

    @Test
    void explore_mergeSequential() {
        // given

        // when
        var mergeSequentialFlux = fluxAndMonoGeneratorService.explore_mergeSequential();

        // then
        StepVerifier.create(mergeSequentialFlux)
                .expectNext("A", "B", "C", "D", "E", "F") // 457 ms (same in this case)
                .verifyComplete();
    }

    @Test
    void explore_zip() {
        // given

        // when
        var zipFlux = fluxAndMonoGeneratorService.explore_zip();

        // then
        StepVerifier.create(zipFlux)
                .expectNext("AD", "BE", "CF")
                .verifyComplete();
    }

    @Test
    void explore_zip_1() {
        // given

        // when
        var zipFlux = fluxAndMonoGeneratorService.explore_zip_1();

        // then
        StepVerifier.create(zipFlux)
                .expectNext("AD14", "BE25", "CF36")
                .verifyComplete();
    }
}