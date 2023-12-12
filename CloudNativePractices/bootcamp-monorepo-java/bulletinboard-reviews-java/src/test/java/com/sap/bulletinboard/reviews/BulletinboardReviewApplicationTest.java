package com.sap.bulletinboard.reviews;

import static org.assertj.core.api.Assertions.assertThat;

import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.sap.bulletinboard.reviews.testutils.RestfulHelper;



@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
public class BulletinboardReviewApplicationTest {
    public static final String ENDPOINT_REVIEWS = "/api/v1/reviews";
    public static final String ENDPOINT_AVERAGE_RATINGS = "/api/v1/averageRatings";
    @LocalServerPort
    private int port;
    private RestfulHelper helper;

    @BeforeEach
    public void setUp() throws Exception {
        helper = RestfulHelper.connect(port);
        ResponseEntity<String> response = helper.delete(ENDPOINT_REVIEWS);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void will_response_empty_reviews_when_no_data() throws JSONException {
        then_I_will_get_reviews("[]");
    }

    @Test
    public void will_response_one_review_when_have_one() throws JSONException {
        given_review_exists("{" +
                "    \"revieweeEmail\": \"john.doe@some.org\"," +
                "    \"reviewerEmail\": \"frank.foe@other.org\"," +
                "    \"rating\": 0," +
                "    \"comment\": \"d'oh\"" +
                "}");

        then_I_will_get_reviews("[{" +
                "    \"revieweeEmail\": \"john.doe@some.org\"," +
                "    \"reviewerEmail\": \"frank.foe@other.org\"," +
                "    \"rating\": 0," +
                "    \"comment\": \"d'oh\"" +
                "}]");
    }

    @Test
    public void will_response_conflict_when_add_review_already_exist_from_same_reviewer() throws JSONException {
        given_review_exists("{" +
                "    \"revieweeEmail\": \"john.doe@some.org\"," +
                "    \"reviewerEmail\": \"frank.foe@other.org\"," +
                "    \"rating\": 0," +
                "    \"comment\": \"original review\"" +
                "}");

        ResponseEntity<String> response = when_I_create_a_review("{" +
                "    \"revieweeEmail\": \"john.doe@some.org\"," +
                "    \"reviewerEmail\": \"frank.foe@other.org\"," +
                "    \"rating\": 1," +
                "    \"comment\": \"still not good\"" +
                "}");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        then_I_will_get_reviews("[{" +
                "    \"revieweeEmail\": \"john.doe@some.org\"," +
                "    \"reviewerEmail\": \"frank.foe@other.org\"," +
                "    \"rating\": 0," +
                "    \"comment\": \"original review\"" +
                "}]");
    }

    @Test
    public void will_response_reviews_from_a_specific_reviewee() throws JSONException {
        given_review_exists("{" +
                "    \"revieweeEmail\": \"john.doe@some.org\"," +
                "    \"reviewerEmail\": \"frank.foe@other.org\"," +
                "    \"rating\": 0," +
                "    \"comment\": \"review for john\"" +
                "}");
        given_review_exists("{" +
                "    \"revieweeEmail\": \"hebe.eddy@some.org\"," +
                "    \"reviewerEmail\": \"georges.gray@another.org\"," +
                "    \"rating\": 1," +
                "    \"comment\": \"review for hebe\"" +
                "}");

        then_I_will_get_reviews_for("john.doe@some.org",
                "[{" +
                "    \"revieweeEmail\": \"john.doe@some.org\"," +
                "    \"reviewerEmail\": \"frank.foe@other.org\"," +
                "    \"rating\": 0," +
                "    \"comment\": \"review for john\"" +
                "}]");
    }

    @Test
    public void should_response_average_rating_for_specific_reviewee() throws JSONException {
        given_review_exists("{" +
                "    \"revieweeEmail\": \"john.doe@some.org\"," +
                "    \"reviewerEmail\": \"frank.foe@other.org\"," +
                "    \"rating\": 0," +
                "    \"comment\": \"review for john\"" +
                "}");
        given_review_exists("{" +
                "    \"revieweeEmail\": \"john.doe@some.org\"," +
                "    \"reviewerEmail\": \"georges.gray@another.org\"," +
                "    \"rating\": 5," +
                "    \"comment\": \"review for john\"" +
                "}");

        JSONAssert.assertEquals("{\"averageRating\": 2.5}", getRating("john.doe@some.org"), true);
    }

    private void then_I_will_get_reviews(String expectedJson) throws JSONException {
        JSONAssert.assertEquals(expectedJson, getReviews(ENDPOINT_REVIEWS), true);
    }

    private void then_I_will_get_reviews_for(String reviewee, String expectedJson) throws JSONException {
        JSONAssert.assertEquals(expectedJson, getReviews(ENDPOINT_REVIEWS+"/"+reviewee), true);
    }

    private void given_review_exists(String body) {
        ResponseEntity<String> response = postReview(body);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    private ResponseEntity<String> when_I_create_a_review(String body) {
        return postReview(body);
    }

    private String getReviews(String endpoint) {
        return helper.successfulGet(endpoint);
    }

    private ResponseEntity<String> postReview(String body) {
        return helper.post(ENDPOINT_REVIEWS, body);
    }

    private String getRating(String reviewee) {
        return helper.successfulGet(ENDPOINT_AVERAGE_RATINGS +"/"+reviewee);
    }

}