package com.sap.cc.bulletinboard.ads;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Disabled
public class SmokeTests {
    private static final String API_PATH = "api/v1";
    private WebTestClient client;
    @Value("${API_URL:}")
    private String apiUrl;

    @BeforeEach
    void setUp() {
        client = WebTestClient
                .bindToServer()
                .baseUrl(apiUrl)
                .responseTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Test
    public void shouldCreatAnAd() {
        Advertisement ad = createAdvertisementWithTitleContactPrice("Piece of Pi(e)", "Gauss", "60");
        Advertisement createdAd = createAd(ad);
        assertThat(createdAd.getTitle()).isEqualTo("Piece of Pi(e)");
        assertThat(createdAd.getContact()).isEqualTo("Gauss");
        assertThat(createdAd.getPrice()).isEqualTo("60");
    }

    @Test
    public void shouldReturnNotAllowedForWrongPostEndpoint() {
        Advertisement ad = createAdvertisementWithTitleContactPrice("Piece of Pi(e)", "Gauss", "60");
        post(basePath() + "/99999999", ad)
                .expectStatus().isEqualTo(405);
    }

    @Test
    public void shouldReturnTheCreatedAd() {
        Advertisement ad = createAdvertisementWithTitleContactPrice("Piece of Pi(e)", "Gauss", "60");
        Long id = createAd(ad).getId();

        EntityExchangeResult<Advertisement> getSingleResponse = get(basePath() + "/" + id)
                .expectStatus().isOk()
                .expectBody(Advertisement.class)
                .returnResult();

        Advertisement responseBody = getSingleResponse.getResponseBody();
        assertThat(responseBody.getId()).isEqualTo(id);
        assertThat(responseBody.getTitle()).isEqualTo("Piece of Pi(e)");
        assertThat(responseBody.getContact()).isEqualTo("Gauss");
        BigDecimal createdPrice = responseBody.getPrice();
        assertThat(new BigDecimal("60").compareTo(createdPrice)).isZero();
    }

    @Test
    public void shouldReturnNotFoundIfAdDoesNotExist() {
        get(basePath() + "/99999999")
                .expectStatus().isNotFound();
    }

    @Test
    public void shouldReturnAListOfAdsIncludingCreatedAd() {
        Advertisement ad = createAdvertisementWithTitleContactPrice("Piece of Pi(e)", "Gauss", "60");
        Long id = createAd(ad).getId();

        EntityExchangeResult<List<Advertisement>> getAllResponse = get(basePath())
                .expectStatus().isOk()
                .expectBodyList(Advertisement.class)
                .returnResult();

        ad = getAllResponse.getResponseBody()
                .stream()
                .filter((a) -> a.getId().equals(id))
                .findFirst()
                .get();
        assertThat(ad.getId()).isEqualTo(id);
        assertThat(ad.getTitle()).isEqualTo("Piece of Pi(e)");
        assertThat(ad.getContact()).isEqualTo("Gauss");
        assertThat(ad.getPrice().compareTo(new BigDecimal("60"))).isZero();
    }

    private Advertisement createAdvertisementWithTitleContactPrice(
            String title, String contact, String price) {
        Advertisement ad = new Advertisement();
        ad.setTitle(title);
        ad.setContact(contact);
        ad.setPrice(new BigDecimal(price));
        return ad;
    }

    private Advertisement createAd(Advertisement ad) {
        EntityExchangeResult<Advertisement> postResponse = post(basePath(), ad)
                .expectStatus().isCreated()
                .expectBody(Advertisement.class)
                .returnResult();

        return postResponse.getResponseBody();
    }

    private ResponseSpec get(String uri) {
        return client
                .get()
                .uri(uri)
                .exchange();
    }

    private ResponseSpec post(String uri, Advertisement ad) {
        return client
                .post()
                .uri(uri)
                .bodyValue(ad)
                .exchange()
                .expectHeader().valueEquals("Content-Type", "application/json");
    }

    private String basePath() {
//        if (apiUrl.contains(".com")) {
//            String cutApiUrl = apiUrl.substring(0, apiUrl.lastIndexOf(".com"));
//            return cutApiUrl + "/" + API_PATH + "/ads";
//        }

        return apiUrl + "/" + API_PATH + "/ads";
    }

}
