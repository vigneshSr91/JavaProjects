package com.sap.cc.bulletinboard.reviews;

public class ReviewRatingResponse {
    private Number average_rating;

    public ReviewRatingResponse(){ }

    public ReviewRatingResponse(Number average_rating){
        this.average_rating = average_rating;
    }

    public Number getAvgRating(){

        return this.average_rating;
    }

    public void setAverage_rating(Number average_rating) {
        this.average_rating = average_rating;
    }
}
