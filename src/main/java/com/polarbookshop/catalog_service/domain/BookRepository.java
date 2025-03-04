package com.polarbookshop.catalog_service.domain;

import java.util.List;
import java.util.Optional;

public interface BookRepository   {

    List<Book> findAll();
    Optional<Book> findByIsbn(String isbn);
    boolean existsByIsbn(String isbn);
    Book save(Book book);
    void deleteByIsbn(String isbn);
}
