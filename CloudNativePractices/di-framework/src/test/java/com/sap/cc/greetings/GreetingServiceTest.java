package com.sap.cc.greetings;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class GreetingServiceTest {
    @MockBean
    Supplier<Greeting> greetingSupplier;
    @Autowired
    GreetingService greetingService;

    @Test
    void getGreetingForPerson() {
        String name = "Angela";
        Mockito.when(greetingSupplier.get()).thenReturn(new Greeting("Hello %s"));
        String greeting = greetingService.greet(name);

        assertThat(greeting).isEqualTo("Hello Angela");
    }
}
