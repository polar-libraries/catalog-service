package com.polarbookshop.catalog_service;

import com.polarbookshop.catalog_service.domain.Book;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
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
        var book = Book.of("1234567890", "Title", "Author", 9.90, "Manning");

        var violations = getConstraintViolations(book);

        assert (violations).isEmpty();
    }


    @Test
    void whenIsbnDefinedButIncorrectThenValidationFails() {

        var book = Book.of("a234567890", "Title", "Author", 9.90, "Manning");
        var violations = getConstraintViolations(book);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("The ISBN format must be valid.");
    }

    @Test
    void whenIsbnIsNullThenValidationFails() {
        var book = Book.of(null, "Title", "Author", 9.90, "Manning");
        var violations = getConstraintViolations(book);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("The book ISBN must be defined.");

    }

    @Test
    void whenTitleIsNullThenValidationFails() {
        var book = Book.of("1234567890", null, "Author", 9.90, "Manning");
        var violations = getConstraintViolations(book);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("The book title must be defined.");
    }

    @Test
    void whenAuthorIsNullThenValidationFails() {
        var book = Book.of("1234567890", "Title", null, 9.90, "Manning");
        var violations = getConstraintViolations(book);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("The book author must be defined.");

    }

    @Test
    void whenPriceIsEqualsZeroAndLessThanZeroThenValidationFails() {
        var book = Book.of("1234567890", "Title", "Author", 0.0, "Manning");
        var violations = getConstraintViolations(book);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("The book price must be greater than zero.");
    }

    @Test
    void whenPriceIsNullThenValidationFails() {
        var book = Book.of("1234567890", "Title", "Author", null, "Manning");
        var violations = getConstraintViolations(book);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("The book price must be defined.");
    }

    @Test
    void whenAllFieldIsNullThenValidationFails() {
        var book = Book.of(null, null, null, null, null);
        var violations = getConstraintViolations(book);

        assertThat(violations).hasSize(5);
        assertThat(violations.stream().map(ConstraintViolation::getMessageTemplate).collect(Collectors.toList()))
                .containsExactlyInAnyOrder(
                        "The book ISBN must be defined.",
                        "The book title must be defined.",
                        "The book author must be defined.",
                        "The book price must be defined.",
                        "The publisher must be defined."
                );
    }

    private static Set<ConstraintViolation<Book>> getConstraintViolations(Book book) {
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        return violations;
    }

}
