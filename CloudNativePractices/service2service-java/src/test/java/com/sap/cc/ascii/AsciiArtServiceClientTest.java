package com.sap.cc.ascii;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sap.cc.InvalidRequestException;
import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.mockwebserver.MockWebServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class AsciiArtServiceClientTest {

    public static final AsciiArtRequest WITH_VALID_ARGS = new AsciiArtRequest("HelloWorld", "3");
    public static final AsciiArtRequest WITH_UNKNOWN_FONT_ID = new AsciiArtRequest("handleThis", "9");

    private AsciiArtServiceUrlProvider asciiArtServiceUrlProvider = Mockito.mock(AsciiArtServiceUrlProvider.class);
    private AsciiArtServiceClient asciiArtServiceClient;
    public static MockWebServer mockBackEnd;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @BeforeEach
    void initialize() {
        String serviceUrl = String.format("http://localhost:%s/api/v1/asciiArt/", mockBackEnd.getPort());
        Mockito.when(asciiArtServiceUrlProvider.getServiceUrl()).thenReturn(serviceUrl);
        asciiArtServiceClient = new AsciiArtServiceClient(WebClient.create(), asciiArtServiceUrlProvider);
    }

    @Test
    public void whenCallingGetAsciiString_thenClientMakesCorrectCallToService(){
        try {
            AsciiArtResponse response = new AsciiArtResponse("'Hello World' as ascii art", "comic");
            mockBackEnd.enqueue(new MockResponse()
                    .setBody(objectMapper.writeValueAsString(response))
                    .addHeader(org.springframework.http.HttpHeaders.CONTENT_TYPE,
                            org.springframework.http.MediaType.APPLICATION_JSON_VALUE));
            assertThat(asciiArtServiceClient.getAsciiString(WITH_VALID_ARGS)).isEqualTo("'Hello World' as ascii art");
        } catch (JsonProcessingException E){

        }
    }

    @Test
    public void whenRequestingWithInvalidRequest_thenInvalidRequestExceptionIsThrown(){
        mockBackEnd.enqueue(new MockResponse().setResponseCode(400)); //You are not a teapot, so change the value.
        assertThatThrownBy(( ) -> asciiArtServiceClient.getAsciiString(WITH_UNKNOWN_FONT_ID))
                .isInstanceOf(InvalidRequestException.class);
    }

}