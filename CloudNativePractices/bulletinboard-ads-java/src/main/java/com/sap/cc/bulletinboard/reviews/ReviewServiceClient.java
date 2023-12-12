package com.sap.cc.bulletinboard.reviews;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
@Service
public class ReviewServiceClient {

    //@Value("${service.reviews.username}")
    private String reviewServiceUsername = "ad496250-65b4-4e16-bb26-55ab4e46763f";

    //@Value("${service.reviews.password}")
    private String reviewServicePassword = "2cd7a62f-e789-447b-a9a1-ca8423f75b99";

    private final WebClient webClient;
    private final ReviewServiceUrlProvider reviewServiceUrlProvider;

    private ReviewRatingResponse reviewResponse;


    public ReviewServiceClient(WebClient webClient, ReviewServiceUrlProvider reviewServiceUrlProvider) {
        this.webClient = webClient;
        this.reviewServiceUrlProvider = reviewServiceUrlProvider;
    }

    public Number getAvgRating(ReviewRatingRequest reviewRatingRequest) {
        String url;
        Number avgRating = 0;
        if (reviewRatingRequest.getEmail() != null) {
            url = this.reviewServiceUrlProvider.getServiceUrl().concat(reviewRatingRequest.getEmail());
            try {

                reviewResponse = this.webClient.get().uri(url)
                        .headers(httpHeaders -> httpHeaders.setBasicAuth(reviewServiceUsername, reviewServicePassword))
//                    .bodyValue(reviewRatingRequest)
                        .retrieve()
                        .bodyToMono(ReviewRatingResponse.class)
                        .block();
                avgRating = reviewResponse.getAvgRating();
            } catch (Exception E) {
                avgRating = 0;
                //throw new IllegalArgumentException("Bad Request", E);
            }
        }
        return avgRating;
    }
}

