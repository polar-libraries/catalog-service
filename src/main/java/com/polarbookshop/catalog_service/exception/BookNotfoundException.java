package com.polarbookshop.catalog_service.exception;

public class BookNotfoundException extends RuntimeException {
    public BookNotfoundException(String isbn) {
        super("The book with ISBN " + isbn + " was not found.");
    }
}

