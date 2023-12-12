package com.sap.cc.bulletinboard.ads;

import com.sap.cc.bulletinboard.reviews.ReviewRatingRequest;
import com.sap.cc.bulletinboard.reviews.ReviewServiceClient;

public class ContactAverageReview {
    private Number average_rating;

    private ReviewServiceClient reviewServiceClient;

    public ContactAverageReview(ReviewServiceClient reviewServiceClient){
        this.reviewServiceClient = reviewServiceClient;
    }
    public Number getAverage_rating(Advertisement advertisement) {
        ReviewRatingRequest request = new ReviewRatingRequest(advertisement.getContact());
        return reviewServiceClient.getAvgRating(request);
    }
}
