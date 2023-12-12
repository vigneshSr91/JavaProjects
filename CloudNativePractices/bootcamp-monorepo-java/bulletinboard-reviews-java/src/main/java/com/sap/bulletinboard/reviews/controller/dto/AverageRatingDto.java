package com.sap.bulletinboard.reviews.controller.dto;

public class    AverageRatingDto {
    Number averageRating;

    public AverageRatingDto() {
    }

    public AverageRatingDto(Number averageRating) {
        this.averageRating = averageRating;
    }

    public Number getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Number averageRating) {
        this.averageRating = averageRating;
    }
}
