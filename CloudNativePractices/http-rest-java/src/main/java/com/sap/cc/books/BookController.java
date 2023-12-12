package com.sap.cc.books;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@Configuration
@RequestMapping("/api/v1/books")
public class BookController {
    @Autowired
    private BookStorage bookStorage;

    public BookController(BookStorage books){
        this.bookStorage = books;
    }

    @GetMapping
    public List<Book> getAllBooks(){
        return bookStorage.getAll();
    }

    @PostMapping
    public ResponseEntity<Book> addBook(@RequestBody Book book) throws Exception{
        Book createdBook = book;
        //createdBook.setId(1L);
        bookStorage.save(createdBook);
        //createdBook.getId()
        UriComponents uriComponents = UriComponentsBuilder
                .fromPath("/api/v1/books" + "/{id}")
                .buildAndExpand(createdBook.getId());
        URI locationHeaderUri = new URI(uriComponents.getPath());

        return ResponseEntity.created(locationHeaderUri).body(createdBook);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Book> getSingle(@PathVariable("id") Long id){
        if(id < 1){
            throw new IllegalArgumentException("Id must not be less than 1");
        }
        if(bookStorage.get(id).isPresent()){
            Book requestedBook = bookStorage.get(id).get();
            return new ResponseEntity<>(requestedBook, HttpStatus.OK);
        } else {
            //return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            throw new NotFoundException();
        }

    }

}
