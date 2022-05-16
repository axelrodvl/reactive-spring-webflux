package com.reactivespring.routes;

import com.reactivespring.domain.Review;
import com.reactivespring.repository.ReviewReactiveRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class ReviewsIntgTest {

    private static final String REVIEWS_URL = "/v1/reviews";
    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ReviewReactiveRepository reviewReactiveRepository;

    @BeforeEach
    void setUp() {
        var reviewsList = List.of(
                new Review(null, 1L, "Awesome Movie", 9.0),
                new Review(null, 1L, "Awesome Movie1", 9.0),
                new Review(null, 2L, "Excellent Movie", 8.0));

        reviewReactiveRepository.saveAll(reviewsList)
                .blockLast();
    }
    @AfterEach
    void tearDown() {
        reviewReactiveRepository.deleteAll().block();
    }

    @Test
    void addReview() {
        // given
        var review = new Review(null, 1L, "Awesome Movie", 9.0);

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
    void getReview() {
        // given

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
    void getReview_movieInfoId() {
        // given
        var movieInfoId = 1L;

        // when
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(REVIEWS_URL)
                        .queryParam("movieInfoId", movieInfoId)
                        .build())
                .exchange()
                .expectBodyList(Review.class)
                .hasSize(2);

        // then
    }

    @Test
    void updateReview() {
        // given
        var movieInfoId = 2L;
        var newComment = "Updated comment";

        // when
        var reviewToUpdate = webTestClient
                .get()
                .uri(REVIEWS_URL + "?movieInfoId={movieInfoId}", movieInfoId)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Review.class)
                .getResponseBody().blockLast();

        assert reviewToUpdate != null;
        reviewToUpdate.setComment(newComment);

        var reviewId = reviewToUpdate.getReviewId();

        webTestClient
                .put()
                .uri(REVIEWS_URL + "/{id}", reviewId)
                .bodyValue(reviewToUpdate)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Review.class)
                .consumeWith(reviewEntityExchangeResult -> {
                    var updatedReview = reviewEntityExchangeResult.getResponseBody();
                    assert updatedReview != null;
                    assertEquals(newComment, updatedReview.getComment());
                });

        // then
    }

    @Test
    void deleteReview() {
        // given
        var movieInfoId = 2L;

        // when
        var reviewToDelete = webTestClient
                .get()
                .uri(REVIEWS_URL + "?movieInfoId={movieInfoId}", movieInfoId)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Review.class)
                .getResponseBody().blockLast();

        assert reviewToDelete != null;
        var reviewId = reviewToDelete.getReviewId();

        webTestClient
                .delete()
                .uri(REVIEWS_URL + "/{id}", reviewId)
                .exchange()
                .expectStatus().isNoContent();

    }
}
