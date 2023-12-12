package com.sap.cc.videostore;

public abstract class Movie {
    private String title;
    //int priceCode;

    public Movie(String title) {
        this.title = title;
        //this.priceCode = priceCode;
    }
    public abstract double determineAmount(int daysRented);

    public abstract int determineFrequentRenterPoints(int daysRented);

    public String getTitle() {
        return title;
    }
}
