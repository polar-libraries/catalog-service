package com.polarbookshop.catalog_service.dto;

import com.polarbookshop.catalog_service.domain.Book;

public record BookResponse
        (String isbn,
         String title,
         String author,
         Double price) {

    public static BookResponse entityToDtoResponse(Book book) {
        return new BookResponse(book.isbn(), book.title(), book.author(), book.price());
    }
}
