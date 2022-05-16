package com.reactivespring.routes;

import com.reactivespring.domain.Review;
import com.reactivespring.exceptionhandler.GlobalErrorHandler;
import com.reactivespring.handler.ReviewHandler;
import com.reactivespring.repository.ReviewReactiveRepository;
import com.reactivespring.router.ReviewRouter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.isA;


@WebFluxTest
@ContextConfiguration(classes = {ReviewRouter.class, ReviewHandler.class, GlobalErrorHandler.class})
@AutoConfigureWebTestClient
public class ReviewsUnitTest {
    private static final String REVIEWS_URL = "/v1/reviews";

    @MockBean
    private ReviewReactiveRepository reviewReactiveRepository;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void addReview() {
        // given
        var review = new Review(null, 1L, "Awesome Movie", 9.0);

        when(reviewReactiveRepository.save(isA(Review.class)))
                .thenReturn(Mono.just(new Review("abc", 1L, "Awesome Movie", 9.0)));

        // when
        webTestClient
                .post()
                .uri(REVIEWS_URL)
                .bodyValue(review)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Review.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var savedReview = movieInfoEntityExchangeResult.getResponseBody();
                    assert savedReview != null;
                    assert savedReview.getReviewId() != null;
                });

        // then
    }

    @Test
    void addReview_validation() {
        // given
        var review = new Review(null, null, "Awesome Movie", -9.0);

        when(reviewReactiveRepository.save(isA(Review.class)))
                .thenReturn(Mono.just(new Review("abc", 1L, "Awesome Movie", 9.0)));

        // when
        webTestClient
                .post()
                .uri(REVIEWS_URL)
                .bodyValue(review)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .isEqualTo("rating.movieInfoId : must not be null,rating.negative : please pass a non-negative value");

        // then
    }

    @Test
    void getReview() {
        // given
        var reviewsList = List.of(
                new Review(null, 1L, "Awesome Movie", 9.0),
                new Review(null, 1L, "Awesome Movie1", 9.0),
                new Review(null, 2L, "Excellent Movie", 8.0));

        when(reviewReactiveRepository.findAll())
                .thenReturn(Flux.fromIterable(reviewsList));

        // when
        webTestClient
                .get()
                .uri(REVIEWS_URL)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Review.class)
                .hasSize(3);

        // then
    }

    @Test
    void updateReview() {
        // given
        var reviewId = "abc";
        var existingReview = new Review(reviewId, 1L, "Awesome Movie", 9.0);
        var updatedReview = new Review(reviewId, 1L, "Updated Awesome Movie", 9.5);

        when(reviewReactiveRepository.findById(isA(String.class)))
                .thenReturn(Mono.just(existingReview));

        when(reviewReactiveRepository.save(isA(Review.class)))
                .thenReturn(Mono.just(updatedReview));

        // when
        webTestClient
                .put()
                .uri(REVIEWS_URL + "/{id}", reviewId)
                .bodyValue(updatedReview)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Review.class)
                .isEqualTo(updatedReview);

        // then
    }

    @Test
    void updateReview_notFound() {
        // given
        var reviewId = "def";
        var updatedReview = new Review(reviewId, 1L, "Updated Awesome Movie", 9.5);

        when(reviewReactiveRepository.findById(isA(String.class)))
                .thenReturn(Mono.empty());

        // when
        webTestClient
                .put()
                .uri(REVIEWS_URL + "/{id}", reviewId)
                .bodyValue(updatedReview)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .isEqualTo("Review not found for the given Review ID def");

        // then
    }

    @Test
    void deleteReview() {
        // given
        when(reviewReactiveRepository.deleteById(isA(String.class)))
                .thenReturn(Mono.empty());

        // when
        webTestClient
                .delete()
                .uri(REVIEWS_URL + "/{id}", 123)
                .exchange()
                .expectStatus().isNoContent();

        // then
    }
}
