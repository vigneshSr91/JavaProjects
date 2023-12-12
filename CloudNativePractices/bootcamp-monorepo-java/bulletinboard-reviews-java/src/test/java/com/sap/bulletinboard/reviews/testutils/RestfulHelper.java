package com.sap.bulletinboard.reviews.testutils;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

public class RestfulHelper {
    private static final String LOCALHOST = "http://localhost:";

    private TestRestTemplate restTemplate = new TestRestTemplate();
    private int port;

    public static RestfulHelper connect(int port) {
        return new RestfulHelper(port);
    }

    public RestfulHelper(int port) {
        this.port = port;
    }

    private String fullUrl(String entry) {
        return LOCALHOST + port + entry;
    }

    public ResponseEntity<String> get(String url) {
        return restTemplate.exchange(RequestEntity
                .get(URI.create(fullUrl(url)))
                .build(), String.class);
    }

    public String successfulGet(String endpoint) {
        ResponseEntity<String> response = get(endpoint);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        return response.getBody();
    }

    public ResponseEntity<String> post(String url, String body) {
        return restTemplate.exchange(RequestEntity
                .post(URI.create(fullUrl(url)))
                .header("Content-Type", "application/json").body(body), String.class);
    }

    public ResponseEntity<String> delete(String url) {
        return restTemplate.exchange(RequestEntity
                .delete(URI.create(fullUrl(url)))
                .build(), String.class);
    }
}
