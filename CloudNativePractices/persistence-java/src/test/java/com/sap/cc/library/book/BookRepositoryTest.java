
package com.sap.cc.library.book;

import com.sun.istack.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
@SpringBootTest
public class BookRepositoryTest {
    @Autowired
    private BookRepository repository;
    @Autowired
    private AuthorRepository authRepo;
    @BeforeEach
    public void clearDB(){
        repository.deleteAll();
    }

    @Test
    public void findAll(){
       List<Book> books = repository.findAll();
        Assertions.assertEquals(books.isEmpty(), Boolean.TRUE);
    }
    @Test
    public void save(){
        Book cleanCode = BookFixtures.cleanCode();
        repository.save(cleanCode);
        List<Book> books = repository.findAll();
        Assertions.assertEquals(books.get(0).getTitle(), cleanCode.getTitle());
        //Assertions.assertEquals(books.get(0).getAuthor().getName(), cleanCode.getAuthor().getName());
    }

    @Test
    public void findBookByTitle(){
        Book cleanCode = BookFixtures.cleanCode();
        repository.save(cleanCode);
        Book designPatterns = BookFixtures.designPatterns();
        repository.save(designPatterns);

        Book book = repository.findByTitle("Clean Code");

        Assertions.assertEquals(book.getTitle(), BookFixtures.cleanCode().getTitle());

    }


}
