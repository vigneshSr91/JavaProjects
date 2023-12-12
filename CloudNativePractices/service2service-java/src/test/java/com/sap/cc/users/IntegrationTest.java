package com.sap.cc.users;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.sap.cc.ascii.AsciiArtResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
                properties = {"service.ascii.url=http://localhost:8181/api/v1/asciiArt/"})
public class IntegrationTest {
    public static MockWebServer mockBackEnd;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start(8181);
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Autowired
    private WebTestClient webTestClient;
    
    @Autowired
    private ObjectMapper objectMapper; 
    
    @Test
    void returnsPrettyPage() {
        try {
            AsciiArtResponse response = new AsciiArtResponse("PrettyName", "comic");
            mockBackEnd.enqueue(new MockResponse()
                    .setBody(objectMapper.writeValueAsString(response))
                    .addHeader(org.springframework.http.HttpHeaders.CONTENT_TYPE,
                            org.springframework.http.MediaType.APPLICATION_JSON_VALUE));
            //assertThat(asciiArtServiceClient.getAsciiString(WITH_VALID_ARGS)).isEqualTo("'Hello World' as ascii art");
        } catch (JsonProcessingException E){
            Assertions.fail(E.getMessage());
        }
        webTestClient
                .get().uri("/api/v1/users/pretty/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).value(body -> {
                    assertThat(body).containsPattern("PrettyName*\\r\\n");
                });
        try {
            RecordedRequest request = mockBackEnd.takeRequest();
            assertEquals("POST", request.getMethod());
            assertEquals("/api/v1/asciiArt/", request.getPath());
        } catch (InterruptedException E){
            Assertions.fail(E.getMessage());
        }
    }
}
