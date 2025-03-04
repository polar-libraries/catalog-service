package com.polarbookshop.catalog_service.domain;

import com.polarbookshop.catalog_service.dto.BookRequest;

public record Book(String isbn, String title, String author, Double price) {

    public static Book createBook(BookRequest bookRequest) {
        return new Book(bookRequest.isbn(), bookRequest.title(), bookRequest.author(), bookRequest.price());
    }
}
