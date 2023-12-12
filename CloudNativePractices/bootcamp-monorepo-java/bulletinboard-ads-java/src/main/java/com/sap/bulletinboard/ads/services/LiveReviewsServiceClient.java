package com.sap.bulletinboard.ads.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class LiveReviewsServiceClient implements ReviewsServiceClient {
    private static final String API_PATH = "api/v1";
    private final WebClient webClient;

    @Value("${REVIEWS_HOST_INTERNAL:}")
    private String reviewsServiceHostInternal;
    @Value("${REVIEWS_HOST:}")
    private String reviewsServiceHost;

    private String determineHost() {
	if (reviewsServiceHostInternal.equals("")) {
	    return reviewsServiceHost;
	}
	return reviewsServiceHostInternal;
    }

    public LiveReviewsServiceClient(WebClient webClient) {
	this.webClient = webClient;
    }

    @Override
    public Double getAverageRating(String userEmail) {
	String url = determineHost() + "/" + API_PATH + "/averageRatings/" + userEmail;

	AverageRating review = webClient.get().uri(url).retrieve().bodyToMono(AverageRating.class).block();
	return review.getAverageRating();
    }
}
