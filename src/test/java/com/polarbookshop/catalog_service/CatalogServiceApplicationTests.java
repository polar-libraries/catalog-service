package com.polarbookshop.catalog_service;

import com.polarbookshop.catalog_service.domain.Book;
import com.polarbookshop.catalog_service.domain.BookRepository;
import com.polarbookshop.catalog_service.dto.BookRequest;
import com.polarbookshop.catalog_service.dto.BookResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
class CatalogServiceApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private BookRepository bookRepository;

    @Test
    void whenPostRequestThenBookCreated() {

        var expectedBook = new BookRequest("1231231230", "Title", "Author", 9.90, "Manning");

        webTestClient.post()
                .uri("/books")
                .bodyValue(expectedBook)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(BookResponse.class).value(actualBook -> {
                    assertThat(actualBook).isNotNull();
                    assertThat(actualBook.isbn()).isEqualTo(expectedBook.isbn());
                });
    }

    @Test
    void whenPostRequestThenExceptionBookAlreadyExistyWhenBookCreated() {

        var expectedBook = Book.of("1231231231", "Title", "Author", 9.90, "Manning");
        bookRepository.save(expectedBook);

        webTestClient.post()
                .uri("/books")
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
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        assertThat(bookRepository.existsByIsbn(expectedBook.isbn())).isFalse();
    }

    @Test
    void whenUpdateRequestThenUpdateBook() {

        var bookUpdate = Book.of("1234567890", "Harry Potter", "Teste", 18.90, "Manning");
        var expectedBook = Book.of("1234567890", "Title", "Author", 9.90, "Manning");

        bookRepository.save(expectedBook);

        webTestClient.put()
                .uri("/books/" + expectedBook.isbn())
                .bodyValue(bookUpdate)
                .exchange()
                .expectStatus().isOk()
                .expectBody(BookResponse.class).value(updateBook -> {
                    compareActualBookWithExpectedBook(updateBook, bookUpdate);
                });
    }


    private void compareActualBookWithExpectedBook(BookResponse actualBook, Book expectedBook) {
        bookRepository.findByIsbn(actualBook.isbn()).ifPresent(savedBook -> {
            assertThat(savedBook.isbn()).isEqualTo(expectedBook.isbn());
            assertThat(savedBook.author()).isEqualTo(expectedBook.author());
            assertThat(savedBook.title()).isEqualTo(expectedBook.title());
            assertThat(savedBook.price()).isEqualTo(expectedBook.price());
        });
    }


}
