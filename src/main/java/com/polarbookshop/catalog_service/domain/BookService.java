package com.polarbookshop.catalog_service.domain;


import com.polarbookshop.catalog_service.dto.BookRequest;
import com.polarbookshop.catalog_service.dto.BookResponse;
import com.polarbookshop.catalog_service.exception.BookAlreadyExistsException;
import com.polarbookshop.catalog_service.exception.BookNotfoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Iterable<BookResponse> viewBookList() {

        return bookRepository.findAll()
                .stream().map(BookResponse::entityToDtoResponse)
                .collect(Collectors.toList());
    }

    public BookResponse viewBookDetails(String isbn) {
        return bookRepository.findByIsbn(isbn)
                .map(BookResponse::entityToDtoResponse)
                .orElseThrow(() -> new BookNotfoundException(isbn));
    }

    public BookResponse addBookToCatalog(BookRequest bookRequest) {

        Optional<Book> existingBook = bookRepository
                .findByIsbn(bookRequest.isbn());

        if (existingBook.isPresent()) {
            throw new BookAlreadyExistsException(bookRequest.isbn());
        }

        var book = bookRepository.save(Book.createBook(bookRequest));
        return BookResponse.entityToDtoResponse(book);
    }

    public void removeBookFromCatalog(String isbn) {
        bookRepository.deleteByIsbn(isbn);
    }

    public BookResponse editBookDetails(String isbn, BookRequest bookRequest) {

        return bookRepository.findByIsbn(isbn)
                .map(existingBook -> {
                    var bookToUpdate = new Book(
                            existingBook.id(),
                            existingBook.isbn(),
                            bookRequest.title(),
                            bookRequest.author(),
                            bookRequest.price(),
                            bookRequest.publisher(),
                            existingBook.createdDate(),
                            existingBook.lastModifiedDate(),
                            existingBook.createdBy(),
                            existingBook.lastModifiedBy(),
                            existingBook.version());
                    return bookRepository.save(bookToUpdate);
                })
                .map(BookResponse::entityToDtoResponse)
                .orElseGet(() -> addBookToCatalog(bookRequest));
    }

}
