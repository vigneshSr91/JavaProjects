package com.sap.cc.books;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions.*;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc

public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookStorage storage;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void beforeEach(){
        storage.deleteAll();
    }

    @Test
    public void getAll_noBooks_returnsEmptyList() throws Exception{
        this.mockMvc.perform(get("/api/v1/books"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("[]")));
    }

    @Test
    public void addBook_returnsCreatedBook() throws Exception{

        Book newBook = new Book();
        String jsonBody = objectMapper.writeValueAsString(newBook);

        MockHttpServletResponse response =
        this.mockMvc.perform(post("/api/v1/books")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        String responseInJSON = response.getContentAsString();
        Book createdBook = objectMapper.readValue(responseInJSON, Book.class);

        assertThat(createdBook.getId(), notNullValue());

    }

    @Test
    public void addBookAndGetSingle_returnsBook() throws Exception{
        // first, add a book
        Book newBook = new Book();
        String jsonBody = objectMapper.writeValueAsString(newBook);

        MockHttpServletResponse response =
                this.mockMvc.perform(post("/api/v1/books")
                                .content(jsonBody)
                                .contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse();

        response =
            this.mockMvc.perform(get(response.getHeader("location")))
                    .andExpect(status().isOk())
                    .andReturn().getResponse();

        String responseInJSON = response.getContentAsString();
        Book createdBook = objectMapper.readValue(responseInJSON, Book.class);

        assertThat(createdBook.getId(), notNullValue());
    }

    @Test
    public void getSingle_noBooks_returnsNotFound() throws Exception{
        MockHttpServletResponse response =
                this.mockMvc.perform(get("/api/v1/books/1"))
                        .andExpect(status().isNotFound())
                        .andReturn().getResponse();
    }

    @Test
    public void addMultipleAndGetAll_returnsAddedBooks() throws Exception{
        Book newBook = new Book();
        String jsonBody = objectMapper.writeValueAsString(newBook);

        this.mockMvc.perform(post("/api/v1/books")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON));

        MockHttpServletResponse response =
                this.mockMvc.perform(get("/api/v1/books"))
                        .andExpect(status().isOk())
                        .andReturn().getResponse();

        List<Book> books = objectMapper
                .readValue(response.getContentAsString(), new com.fasterxml.jackson.core.type.TypeReference<List<Book>>() {});

        Assertions.assertEquals(books.size(), 1);
        assertThat(books.get(0).getId(), notNullValue());

        newBook = new Book();
        jsonBody = objectMapper.writeValueAsString(newBook);

        this.mockMvc.perform(post("/api/v1/books")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON));

        response =
                this.mockMvc.perform(get("/api/v1/books"))
                        .andExpect(status().isOk())
                        .andReturn().getResponse();

        books = objectMapper
                .readValue(response.getContentAsString(), new com.fasterxml.jackson.core.type.TypeReference<List<Book>>() {});

        Assertions.assertEquals(books.size(), 2);
    }

    @Test
    public void getSingle_idLessThanOne_returnsBadRequest() throws Exception{
        this.mockMvc.perform(get("/api/v1/books/0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getSingle_idNotExisting_returnsNotFound() throws Exception{
        this.mockMvc.perform(get("/api/v1/books/111"))
                .andExpect(status().isNotFound());
    }

}
