package com.sap.cc.bulletinboard.reviews;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sap.cc.bulletinboard.reviews.ReviewRatingRequest;
import com.sap.cc.bulletinboard.reviews.ReviewRatingResponse;
import com.sap.cc.bulletinboard.reviews.ReviewServiceClient;
import com.sap.cc.bulletinboard.reviews.ReviewServiceUrlProvider;
import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.mockwebserver.MockWebServer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
@SpringBootTest
@AutoConfigureMockMvc
public class ReviewServiceClientTest {
    public static MockWebServer mockBackEnd;

    public static final ReviewRatingRequest WITH_VALID_ARGS = new ReviewRatingRequest("abc@gmail.com");

    public static final ReviewRatingRequest WITH_UNKNOWN_CONTACT = new ReviewRatingRequest("unknown@abc.com");
    private ReviewServiceUrlProvider reviewServiceUrlProvider = Mockito.mock(ReviewServiceUrlProvider.class);

    private ReviewServiceClient reviewServiceClient;
    private ObjectMapper objectMapper = new ObjectMapper();
    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @BeforeEach
    void initialize() {
        String serviceUrl = String.format("http://localhost:%s/api/v1/averageRatings/", mockBackEnd.getPort());
        Mockito.when(reviewServiceUrlProvider.getServiceUrl()).thenReturn(serviceUrl);
        reviewServiceClient = new ReviewServiceClient(WebClient.create(), reviewServiceUrlProvider);
    }

    @Test
    public void whenCallingGetAvgRating_thenClientMakesCorrectCallToService(){
        try{
            ReviewRatingResponse response = new ReviewRatingResponse(3.0);
            mockBackEnd.enqueue(new MockResponse()
                    .setBody(objectMapper.writeValueAsString(response))
                    .addHeader(org.springframework.http.HttpHeaders.CONTENT_TYPE,
                            org.springframework.http.MediaType.APPLICATION_JSON_VALUE ) );
            assertThat(reviewServiceClient.getAvgRating(WITH_VALID_ARGS)).isEqualTo(null);
        } catch (JsonProcessingException E){

        }
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }
}
