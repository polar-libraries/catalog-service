package com.polarbookshop.catalog_service.web;

import com.polarbookshop.catalog_service.controller.BookController;
import com.polarbookshop.catalog_service.domain.BookService;
import com.polarbookshop.catalog_service.exception.BookNotfoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
public class BookControllerMvcTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @Test
    void whenGetBookNotExistingThenShouldReturn404() throws Exception {
        String isbn = "73737313940";
        given(bookService.viewBookDetails(isbn))
                .willThrow(BookNotfoundException.class);

        mockMvc.perform(get("/books/" + isbn))
                .andExpect(status().isNotFound());
    }

}