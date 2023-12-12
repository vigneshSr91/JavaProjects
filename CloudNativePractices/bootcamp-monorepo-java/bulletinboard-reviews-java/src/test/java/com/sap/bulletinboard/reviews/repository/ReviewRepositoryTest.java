package com.sap.bulletinboard.reviews.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.sap.bulletinboard.reviews.models.Review;

@DataJpaTest
public class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository repository;

    @BeforeEach
    public void setup() {
        repository.deleteAllInBatch();
    }

    @AfterEach
    public void teardown() {
        repository.deleteAllInBatch();
    }

    @Test
    public void givenNoReviews_thenNoAverageRating() {
        Number averageRating = repository.getAvgRatingByIdRevieweeEmail("john.doe@acme.org");
        assertThat(averageRating).isNull();
    }

    @Test
    public void givenTwoReviews_thenCalculateAverageRating() {
        createReviewForJohnFrom("jane.roe@acme.org", 5);
        createReviewForJohnFrom("frank.foe@acme.org", 2);
        Number averageRating = repository.getAvgRatingByIdRevieweeEmail("john.doe@acme.org");
        assertThat(averageRating).isEqualTo(3.5);
    }

    private void createReviewForJohnFrom(String reviewer, int rating) {
        Review review = new Review();
        review.setRating(rating);
        Review.ReviewIdentity id = new Review.ReviewIdentity();
        id.setRevieweeEmail("john.doe@acme.org");
        id.setReviewerEmail(reviewer);
        review.setId(id);
        repository.save(review);
    }
}
