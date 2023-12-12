package com.sap.cc.ascii;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.sap.cc.InvalidRequestException;

@Service
public class AsciiArtServiceClient {

    @Value("${service.ascii.username}")
    private String asciiServiceUsername = "";

    @Value("${service.ascii.password}")
    private String asciiServicePassword = "";

    private final WebClient webClient;
    private final AsciiArtServiceUrlProvider asciiArtServiceUrlProvider;

    private AsciiArtResponse asciiArtResponse;

    public AsciiArtServiceClient(WebClient webClient, AsciiArtServiceUrlProvider asciiArtServiceUrlProvider) {
        this.webClient = webClient;
        this.asciiArtServiceUrlProvider = asciiArtServiceUrlProvider;
    }

    public String getAsciiString(AsciiArtRequest asciiArtRequest) {
        try {
            String url = this.asciiArtServiceUrlProvider.getServiceUrl();
            asciiArtResponse = this.webClient.post().uri(url)
                    .headers(httpHeaders -> httpHeaders.setBasicAuth(asciiServiceUsername, asciiServicePassword))
                    .bodyValue(asciiArtRequest)
                    .retrieve()
                    .bodyToMono(AsciiArtResponse.class)
                    .block();
            return asciiArtResponse.getBeautifiedText();
        } catch (WebClientResponseException.BadRequest E) {
            throw new InvalidRequestException("Bad Request", E);
        }
    }
}