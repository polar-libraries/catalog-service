package com.polarbookshop.catalog_service.dto;

public record BookRequest
        (String isbn,
         String title,
         String author,
         Double price) {
}
