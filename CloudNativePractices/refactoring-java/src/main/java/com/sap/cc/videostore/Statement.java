package com.sap.cc.videostore;

import java.util.ArrayList;
import java.util.List;

class Statement {
    private String _name;
    private List<Rental> _rentals = new ArrayList<>();
    private double totalAmount;
    private int frequentRenterPoints;

    public Statement(String name) {
        _name = name;
    }

    public void addRental(Rental arg) {
        _rentals.add(arg);
    }

    public String getName() {
        return _name;
    }

    public String generate() {
        clearTotals();
        String statementText = header();
        statementText += rentalLines();
        //add footer lines
        statementText += footer();
        return statementText;
    }

    private String footer() {
        String footerText = "";
        footerText += "Amount owed is " + String.valueOf(totalAmount) +
                "\n";
        footerText += "You earned " + String.valueOf(frequentRenterPoints)
                +
                " frequent renter points";
        return footerText;
    }

    private String rentalLines() {
        String rentalLinesText = "";
        for (Rental rental : _rentals) {
            rentalLinesText += rentalLine(rental);
        }
        return rentalLinesText;
    }

    private String rentalLine(Rental rental) {

        String rentalLineText = "";
        //determine amounts for rental line
        double rentalAmount = rental.determineAmount();
        // add frequent renter points
        frequentRenterPoints += rental.determineFrequentRenterPoints();
        //show figures for this rental
        rentalLineText = formatRentalLine(rental, rentalLineText, rentalAmount);
        totalAmount += rentalAmount;
        return rentalLineText;
    }

    private static String formatRentalLine(Rental rental, String rentalLineText, double rentalAmount) {
        rentalLineText += "\t" + rental.getMovie().getTitle() + "\t" +
                String.valueOf(rentalAmount) + "\n";
        return rentalLineText;
    }
    private String header() {
        String statementText = "Rental Record for " + getName() + "\n";
        return statementText;
    }

    private void clearTotals() {
        totalAmount = 0;
        frequentRenterPoints = 0;
    }
}
