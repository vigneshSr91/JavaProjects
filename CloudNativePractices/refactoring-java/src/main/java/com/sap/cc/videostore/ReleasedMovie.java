package com.sap.cc.videostore;

public class ReleasedMovie extends Movie {
    public ReleasedMovie(String name) {
        super(name);
    }

    @Override
    public double determineAmount(int daysRented) {
        double rentalAmount = 0;
        rentalAmount += daysRented * 3;

        return rentalAmount;
    }

    @Override
    public int determineFrequentRenterPoints(int daysRented) {
        int frequentRenterPoints = 1;
        // add bonus for a two day new release rental
        if (daysRented > 1) frequentRenterPoints++;

        return frequentRenterPoints;
    }
}
