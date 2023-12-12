package com.sap.bulletinboard.reviews.repository;

import com.sap.bulletinboard.reviews.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Review.ReviewIdentity> {
    List<Review> findByIdRevieweeEmail(String revieweeEmail);

    @Query("SELECT AVG(CAST(r.rating AS float)) from Review r where r.id.revieweeEmail = :revieweeEmail")
    Number getAvgRatingByIdRevieweeEmail(String revieweeEmail);
}
