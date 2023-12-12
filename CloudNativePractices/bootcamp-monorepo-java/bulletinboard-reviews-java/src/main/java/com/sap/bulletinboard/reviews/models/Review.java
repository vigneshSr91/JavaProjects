package com.sap.bulletinboard.reviews.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "reviews")
public class Review {
    @EmbeddedId
    private ReviewIdentity id;
    private Integer rating;
    private String comment;

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ReviewIdentity getId() {
        return id;
    }

    public void setId(ReviewIdentity id) {
        this.id = id;
    }

    @Embeddable
    public static class ReviewIdentity implements Serializable {
        private static final long serialVersionUID = 2160746433109827301L;
        
        @NotNull
        private String revieweeEmail;
        @NotNull
        private String reviewerEmail;

        public String getRevieweeEmail() {
            return revieweeEmail;
        }

        public void setRevieweeEmail(String revieweeEmail) {
            this.revieweeEmail = revieweeEmail;
        }

        public String getReviewerEmail() {
            return reviewerEmail;
        }

        public void setReviewerEmail(String reviewerEmail) {
            this.reviewerEmail = reviewerEmail;
        }
    }
}
