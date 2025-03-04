package com.polarbookshop.catalog_service;

import com.polarbookshop.catalog_service.domain.Book;
import jakarta.validation.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


public class BookValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAllFieldsCorrectThenValidationSucceeds() {
        var book = new Book("1234567890", "Title", "Author", 9.90);

        var violations = getConstraintViolations(book);

        assert (violations).isEmpty();
    }


    @Test
    void whenIsbnDefinedButIncorrectThenValidationFails() {

        var book = new Book("a234567890", "Title", "Author", 9.90);
        var violations = getConstraintViolations(book);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("The ISBN format must be valid.");
    }

    @Test
    void whenIsbnIsNullThenValidationFails() {
        var book = new Book(null, "Title", "Author", 9.90);
        var violations = getConstraintViolations(book);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("The book ISBN must be defined.");

    }

    @Test
    void whenTitleIsNullThenValidationFails() {
        var book = new Book("1234567890", null, "Author", 9.90);
        var violations = getConstraintViolations(book);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("The book title must be defined.");
    }

    @Test
    void whenAuthorIsNullThenValidationFails() {
        var book = new Book("1234567890", "Title", null, 9.90);
        var violations = getConstraintViolations(book);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("The book author must be defined.");

    }

    @Test
    void whenPriceIsEqualsZeroAndLessThanZeroThenValidationFails() {
        var book = new Book("1234567890", "Title", "Author", 0.0);
        var violations = getConstraintViolations(book);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("The book price must be greater than zero.");
    }

    @Test
    void whenPriceIsNullThenValidationFails() {
        var book = new Book("1234567890", "Title", "Author", null);
        var violations = getConstraintViolations(book);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("The book price must be defined.");
    }

    @Test
    void whenAllFieldIsNullThenValidationFails() {
        var book = new Book(null, null, null, null);
        var violations = getConstraintViolations(book);

        assertThat(violations).hasSize(4);
        assertThat(violations.stream().map(ConstraintViolation::getMessageTemplate).collect(Collectors.toList()))
                .containsExactlyInAnyOrder(
                        "The book ISBN must be defined.",
                        "The book title must be defined.",
                        "The book author must be defined.",
                        "The book price must be defined."
                );
    }

    private static Set<ConstraintViolation<Book>> getConstraintViolations(Book book) {
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        return violations;
    }

}
