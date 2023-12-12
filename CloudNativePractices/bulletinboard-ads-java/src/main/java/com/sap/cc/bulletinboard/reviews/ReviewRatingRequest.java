package com.sap.cc.bulletinboard.reviews;

public class ReviewRatingRequest {
    private String email;

    public ReviewRatingRequest(String email){
        this.email = email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getEmail(){
        return this.email;
    }
}
