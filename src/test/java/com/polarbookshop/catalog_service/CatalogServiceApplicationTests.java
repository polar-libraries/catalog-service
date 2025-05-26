package com.polarbookshop.catalog_service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.polarbookshop.catalog_service.domain.Book;
import com.polarbookshop.catalog_service.domain.BookRepository;
import com.polarbookshop.catalog_service.dto.BookResponse;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
@Testcontainers
class CatalogServiceApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private BookRepository bookRepository;

    private static KeycloakToken isabelleTokens;
    private static KeycloakToken bjornTokens;

    @Container
    private static final KeycloakContainer keycloakContainer = new KeycloakContainer("quay.io/keycloak/keycloak:24.0")
            .withRealmImportFile("/test-realm-config.json");

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> keycloakContainer.getAuthServerUrl() + "/realms/PolarBookshop");
    }

    @BeforeAll
    static void generatedAccesTokens() {
        WebClient webClient = WebClient.builder()
                .baseUrl(keycloakContainer.getAuthServerUrl()
                        + "/realms/PolarBookshop/protocol/openid-connect/token")
                .defaultHeader(HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();

        isabelleTokens = authenticateWith(
                "isabelle", "password", webClient);
        bjornTokens = authenticateWith(
                "bjorn", "password", webClient);
    }


    @Test
    void whenPostRequestThenBookCreated() {
        var expectedBook = Book.of("1231231231", "Title", "Author", 9.90, "Polarsophia");

        webTestClient
                .post()
                .uri("/books")
                .headers(headers -> headers.setBearerAuth(isabelleTokens.accessToken()))
                .bodyValue(expectedBook)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Book.class).value(actualBook -> {
                    assertThat(actualBook).isNotNull();
                    assertThat(actualBook.isbn()).isEqualTo(expectedBook.isbn());
                });
    }

    @Test
    void whenPostRequestThenExceptionBookAlreadyExistyWhenBookCreated() {

        var expectedBook = Book.of("1231231271", "Title", "Author", 9.90, "Manning");
        bookRepository.save(expectedBook);

        webTestClient.post()
                .uri("/books")
                .headers(httpHeaders -> httpHeaders.setBearerAuth(isabelleTokens.accessToken()))
                .bodyValue(expectedBook)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(String.class)
                .isEqualTo("A book with ISBN " + expectedBook.isbn() + " already exists.");
    }

    @Test
    void whenGetRequestThenBook() {

        var expectedBook = Book.of("1231231232", "Title",
                "Author", 9.90, "Manning");

        var expectedBook2 = Book.of("9879879876", "Title2",
                "Author2", 19.90, "Manning");


        bookRepository.save(expectedBook);
        bookRepository.save(expectedBook2);

        webTestClient.get()
                .uri("/books")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(BookResponse.class)
                .value(actualBook -> {
                    assertThat(actualBook).isNotEmpty();
                    assertThat(actualBook).hasSize(2);
                    assertThat(actualBook.get(0).isbn()).isEqualTo(expectedBook.isbn());
                    assertThat(actualBook.get(1).isbn()).isEqualTo(expectedBook2.isbn());

                });
    }


    @Test
    void whenGetRequestWhenFindBookDetails() {

        var expectedBook = Book.of("9879879874", "Title",
                "Author", 9.90, "Manning");

        bookRepository.save(expectedBook);

        webTestClient.get()
                .uri("/books/" + expectedBook.isbn())
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookResponse.class)
                .value(actualBook -> {
                    compareActualBookWithExpectedBook(actualBook, expectedBook);
                });
    }

    @Test
    void whenGetRequestExceptionWhenFindBookDetails() {
        bookRepository.deleteByIsbn("1234567890");

        webTestClient.get()
                .uri("/books/" + "1234567890")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenDeleteRequestWhenFindBook() {
        var expectedBook = Book.of("1231231233", "Title",
                "Author", 9.90, "Manning");

        bookRepository.save(expectedBook);

        webTestClient.delete()
                .uri("/books/" + expectedBook.isbn())
                .headers(httpHeaders -> httpHeaders.setBearerAuth(isabelleTokens.accessToken()))
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        assertThat(bookRepository.existsByIsbn(expectedBook.isbn())).isFalse();
    }

    @Test
    void whenUpdateRequestThenUpdateBook() {

        var bookIsbn = "1234567890";
        var bookToCreate = Book.of(bookIsbn, "Title", "Author", 9.90, "Polarsophia");

        Book createdBook = webTestClient.post()
                .uri("/books")
                .headers(httpHeaders -> httpHeaders.setBearerAuth(isabelleTokens.accessToken()))
                .bodyValue(bookToCreate)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Book.class).value(book -> assertThat(book).isNotNull())
                .returnResult().getResponseBody();

        var bookToUpdate = new Book(createdBook.id(), createdBook.isbn(), "Harry Potter", "Erick Silva", 25.95,
                createdBook.publisher(), createdBook.createdDate(), createdBook.lastModifiedDate(),
                createdBook.createdBy(), createdBook.lastModifiedBy(), createdBook.version());

        webTestClient.put()
                .uri("/books/" + bookIsbn)
                .headers(httpHeaders -> httpHeaders.setBearerAuth(isabelleTokens.accessToken()))
                .bodyValue(bookToUpdate)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Book.class).value(actualBook -> {
                    assertThat(actualBook).isNotNull();
                    assertThat(actualBook.price()).isEqualTo(bookToUpdate.price());
                    assertThat(actualBook.isbn()).isEqualTo(bookToUpdate.isbn());
                });

    }

    @Test
    void whenPostRequestUnauthorizedThen403() {

        var expectedBook = Book.of("1231231271", "Title", "Author",
                9.90, "Polarsophia");

        webTestClient.post()
                .uri("/books")
                .headers(httpHeaders -> httpHeaders.setBearerAuth(bjornTokens.accessToken()))
                .bodyValue(expectedBook)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void whenPostRequestUnauthenticatedThen401() {
        var expectedBook = Book.of("1231231281", "Title", "Author",
                9.90, "Polarsophia");

        webTestClient.post().uri("/books")
                .bodyValue(expectedBook)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    private void compareActualBookWithExpectedBook(BookResponse actualBook, Book bookUpdate) {
        assertThat(actualBook.isbn()).isEqualTo(bookUpdate.isbn());
        assertThat(actualBook.author()).isEqualTo(bookUpdate.author());
        assertThat(actualBook.title()).isEqualTo(bookUpdate.title());
        assertThat(actualBook.price()).isEqualTo(bookUpdate.price());
    }


    private static KeycloakToken authenticateWith(String username, String password, WebClient webClient) {
        return webClient
                .post()
                .body(BodyInserters.fromFormData("grant_type", "password")
                        .with("client_id", "polar-test")
                        .with("username", username)
                        .with("password", password)
                )
                .retrieve()
                .bodyToMono(KeycloakToken.class)
                .block();
    }

    private record KeycloakToken(String accessToken) {

        @JsonCreator
        private KeycloakToken(@JsonProperty("access_token") final String accessToken) {
            this.accessToken = accessToken;
        }

    }

}
