package com.polarbookshop.catalog_service.domain;

import com.polarbookshop.catalog_service.dto.BookRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record BookTest(



        @NotBlank(message = "The book title must be defined.")
        String title,

        @NotBlank(message = "The book author must be defined.")
        String author,

        @NotNull(message = "The book price must be defined.")
        @Positive(message = "The book price must be greater than zero.")
        Double price
) {

    public static BookTest createBook(BookRequest bookRequest) {
        return new BookTest(bookRequest.title(), bookRequest.author(), bookRequest.price());
    }
}
