package com.sap.cc.library.book;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DataDurabilityTest {
    @Autowired
    private BookRepository repository;


    @Test
    public void populateDb(){
        Book cleanCode = BookFixtures.cleanCode();
        repository.save(cleanCode);
        Book designPatterns = BookFixtures.designPatterns();
        repository.save(designPatterns);
        assertThat(repository.findAll()).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    public void isDbPopulated(){
        assertThat(repository.findAll()).hasSizeGreaterThanOrEqualTo(2);
    }
}
