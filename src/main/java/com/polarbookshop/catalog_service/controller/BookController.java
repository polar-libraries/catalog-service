package com.polarbookshop.catalog_service.controller;

import com.polarbookshop.catalog_service.domain.BookService;
import com.polarbookshop.catalog_service.dto.BookRequest;
import com.polarbookshop.catalog_service.dto.BookResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public Iterable<BookResponse> get() {
        return bookService.viewBookList();
    }

    @GetMapping("{isbn}")
    public BookResponse getByIsbn(@PathVariable String isbn) {
        return bookService.viewBookDetails(isbn);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse post(@RequestBody BookRequest request) {
        return bookService.addBookToCatalog(request);
    }


    @DeleteMapping("{isbn}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String isbn) {
        bookService.removeBookFromCatalog(isbn);
    }

    @PutMapping("{isbn}")
    public BookResponse put(@PathVariable String isbn, @RequestBody BookRequest request) {
        return bookService.editBookDetails(isbn, request);
    }

}
