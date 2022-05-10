package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MovieInfoService;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = MoviesInfoController.class)
@AutoConfigureWebTestClient
public class MoviesInfoControllerUnitTest {
    public static final String MOVIE_INFO_URL = "/v1/movieinfos";
    @Autowired
    private WebTestClient webTestClient;

    @MockBean // Inject Spring Bean into a context
    private MovieInfoService movieInfoServiceMock;

    @Test
    void getAllMoviesInfo() {
        var movieinfos = List.of(new MovieInfo(null, "Batman Begins",
                        2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight",
                        2008, List.of("Christian Bale", "HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises",
                        2012, List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20")));

        when(movieInfoServiceMock.getAllMovieInfos())
                .thenReturn(Flux.fromIterable(movieinfos));

        webTestClient
                .get()
                .uri(MOVIE_INFO_URL)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);
    }

    @Test
    void getMovieInfoById() {
        var movieInfo = new MovieInfo(null, "Batman Begins",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        when(movieInfoServiceMock.getMovieInfoById(anyString()))
                .thenReturn(Mono.just(movieInfo));

        webTestClient
                .get()
                .uri(MOVIE_INFO_URL + "/{id}", "abc")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(MovieInfo.class);
    }

    @Test
    void addMovieInfo() {
        // when
        var movieInfo = new MovieInfo(null, "Batman Begins",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));
        var newId = UUID.randomUUID().toString();

        when(movieInfoServiceMock.addMovieInfo(isA(MovieInfo.class)))
                .thenAnswer((Answer<Mono<MovieInfo>>) invocation -> {
                    var movieInfo1 = invocation.getArgument(0, MovieInfo.class);
                    movieInfo1.setMovieInfoId(newId);
                    return Mono.just(movieInfo1);
                });

        webTestClient
                .post()
                .uri(MOVIE_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var createdMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assert createdMovieInfo != null;
                    assert createdMovieInfo.getMovieInfoId() != null;
                    assertEquals(newId, createdMovieInfo.getMovieInfoId());
                });
    }

    @Test
    void addMovieInfo_validation() {
        // when
        var movieInfo = new MovieInfo(null, "",
                -2005, List.of(""), LocalDate.parse("2005-06-15"));

        webTestClient
                .post()
                .uri(MOVIE_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .consumeWith(stringEntityExchangeResult -> {
                    var responseBody = stringEntityExchangeResult.getResponseBody();
                    System.out.println("responseBody: " + responseBody);
                    var errorMessage = "movieInfo.cast must be present,movieInfo.name must be present,movieInfo.year must be a Positive value";
                    assert responseBody != null;
                    assertEquals(errorMessage, responseBody);
                });
    }

    @Test
    void updateMovieInfo() {
        // given
        var movieInfo = new MovieInfo(null, "Batman Begins",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));
        var newId = UUID.randomUUID().toString();

        when(movieInfoServiceMock.updateMovieInfo(isA(MovieInfo.class), isA(String.class)))
                .thenAnswer((Answer<Mono<MovieInfo>>) invocation -> {
                    var movieInfo1 = invocation.getArgument(0, MovieInfo.class);
                    movieInfo1.setMovieInfoId(newId);
                    return Mono.just(movieInfo1);
                });

        // when
        webTestClient
                .put()
                .uri(MOVIE_INFO_URL + "/{id}", newId)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var createdMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assert createdMovieInfo != null;
                    assert createdMovieInfo.getMovieInfoId() != null;
                    assertEquals(newId, createdMovieInfo.getMovieInfoId());
                });
    }

    @Test
    void deleteMovieInfoById() {
        // given
        var id = UUID.randomUUID().toString();

        when(movieInfoServiceMock.deleteMovieInfo(isA(String.class)))
                .thenReturn(Mono.empty());

        // when
        webTestClient
                .delete()
                .uri(MOVIE_INFO_URL + "/{id}", id)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(Void.class);
    }
}
