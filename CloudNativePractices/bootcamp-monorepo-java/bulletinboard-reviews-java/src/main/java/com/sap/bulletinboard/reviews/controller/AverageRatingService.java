package com.sap.bulletinboard.reviews.controller;

import com.sap.bulletinboard.reviews.controller.dto.AverageRatingDto;
import com.sap.bulletinboard.reviews.repository.ReviewRepository;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AverageRatingService {
    private final ReviewRepository reviewRepository;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public AverageRatingService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @RateLimiter(name = "ratingServiceRateLimiter")
    public AverageRatingDto getAverageRatingDto(String reviewee) {
        return calculateAverageRating(reviewee);
    }

    private AverageRatingDto calculateAverageRating(String reviewee) {
        Number averageRating = reviewRepository.getAvgRatingByIdRevieweeEmail(reviewee);
        if (averageRating == null) {
            logger.info("No ratings found for {}", reviewee);
        }
        return new AverageRatingDto(averageRating);
    }
}
