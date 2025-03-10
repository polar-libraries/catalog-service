package com.polarbookshop.catalog_service.domain;


import com.polarbookshop.catalog_service.dto.BookRequest;
import com.polarbookshop.catalog_service.dto.BookResponse;
import com.polarbookshop.catalog_service.exception.BookNotfoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @InjectMocks
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    private BookRequest expectedBook;
    private Book book;

    @BeforeEach
     void setup() {
        expectedBook = new BookRequest("1231231231", "Title",
                "Author", 9.90);

        book = Book.of("1231231231", "Title",
                "Author", 9.90);
    }

    @Test
     void viewBookDetails() {
        when(bookRepository.findByIsbn("1231231231"))
                .thenReturn(Optional.of(book));

        var findBook = bookService.viewBookDetails("1231231231");

        byCompareActualWithExpect(findBook);
    }

    @Test
     void whenVewBookDetailsShoulThrowBookNotfoundException() {
        String isbn = "1231231231";

        when(bookRepository.findByIsbn(any()))
                .thenThrow(new BookNotfoundException(isbn));

        assertThatExceptionOfType(BookNotfoundException.class)
                .isThrownBy(() -> bookService.viewBookDetails("1231231231"))
                .withMessage("The book with ISBN " + isbn + " was not found.");
    }


    private void byCompareActualWithExpect(BookResponse findBook) {
        assertThat(findBook.isbn()).isEqualTo(book.isbn());
        assertThat(findBook.title()).isEqualTo(book.title());
        assertThat(findBook.author()).isEqualTo(book.author());
        assertThat(findBook.price()).isEqualTo(book.price());
    }

}