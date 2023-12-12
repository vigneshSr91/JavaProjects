package com.sap.cc.videostore;

class Rental {
    private Movie movie;
    private int daysRented;

    public Rental(Movie movie, int daysRented) {
        this.movie = movie;
        this.daysRented = daysRented;
    }
    public int getDaysRented() {

        return daysRented;
    }

    public Movie getMovie() {

        return movie;
    }

    public int determineFrequentRenterPoints(){
        return movie.determineFrequentRenterPoints(daysRented);
    }

    public double determineAmount(){
        return movie.determineAmount(daysRented);
    }

}