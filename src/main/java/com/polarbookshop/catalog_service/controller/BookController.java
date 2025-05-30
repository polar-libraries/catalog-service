package com.polarbookshop.catalog_service.controller;

import com.polarbookshop.catalog_service.domain.BookService;
import com.polarbookshop.catalog_service.dto.BookRequest;
import com.polarbookshop.catalog_service.dto.BookResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("books")
public class BookController {

    private static Logger log = LoggerFactory.getLogger(BookController.class);
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public Iterable<BookResponse> get() {
        log.info("Fetching the list of books in the catalog.");
        return bookService.viewBookList();
    }

    @GetMapping("{isbn}")
    public BookResponse getByIsbn(@PathVariable String isbn) {
        log.info("Fetching the book with ISBN {} from the catalog", isbn);
        return bookService.viewBookDetails(isbn);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse post(@Valid @RequestBody BookRequest request) {
        log.info("Adding a new book to the catalog with ISBN {}", request.isbn());
        return bookService.addBookToCatalog(request);
    }


    @DeleteMapping("{isbn}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String isbn) {
        log.info("Deleting book with ISBN {}", isbn);
        bookService.removeBookFromCatalog(isbn);
    }

    @PutMapping("{isbn}")
    public BookResponse put(@PathVariable String isbn, @Valid @RequestBody BookRequest request) {
        log.info("Updating book with ISBN {}", isbn);
        return bookService.editBookDetails(isbn, request);
    }

}
