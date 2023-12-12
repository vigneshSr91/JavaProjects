package com.sap.cc.bulletinboard.ads;

import com.sap.cc.bulletinboard.NotFoundException;
import com.sap.cc.bulletinboard.reviews.ReviewServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import org.slf4j.MDC;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@RestController
@Configuration
@RequestMapping("/api/v1/ads")
public class AdvertisementController {
    public static final String contactAverageEndpoint = "https://bulletinboard-reviews-cdf.cfapps.eu10.hana.ondemand.com/api/v1/averageRatings/";
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private AdvertisementRepository adStorage;

    private ReviewServiceClient reviewServiceClient;

    private Advertisement advertisement;

    public AdvertisementController(AdvertisementRepository advertisementStorage, ReviewServiceClient reviewServiceClient) {
        this.adStorage = advertisementStorage;
        this.reviewServiceClient = reviewServiceClient;
    }

    @GetMapping
    public List<Advertisement> getAllAds() {
        MDC.put("path","api/v1/ads/");
        //MDC.clear();
        List<Advertisement> advertisements = this.adStorage.findAll();
        for (Advertisement advertisement:advertisements) {
            getAverageRating(advertisement);
        }
        MDC.clear();
        return advertisements;
    }

    @PostMapping
    public ResponseEntity<Advertisement> postAd(@RequestBody Advertisement advertisement) throws Exception{
        MDC.put("path","api/v1/ads/");
        this.advertisement = advertisement;

        adStorage.save(advertisement);
        getAverageRating(advertisement);
        UriComponents uriComponents = UriComponentsBuilder
                .fromPath("/api/v1/ads" + "/{id}")
                .buildAndExpand(advertisement.getId());
        URI locationHeaderUri = new URI(uriComponents.getPath());
        MDC.clear();
        return ResponseEntity.created(locationHeaderUri).body(advertisement);

    }

    @GetMapping("/{id}")
    public ResponseEntity<Advertisement> getAd(@PathVariable("id") Long id){
        MDC.put("path","api/v1/ads/"+id);
        logger.info("Get request for ad id {}",id);
        if(id < 1){
            IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Id must not be less than 1");
            logger.warn(illegalArgumentException.getStackTrace().toString());
            MDC.clear();
            throw new IllegalArgumentException("Id must not be less than 1");
        }
        if(adStorage.findById(id).isPresent()){
            advertisement = adStorage.findById(id).get();
            getAverageRating(advertisement);
            logger.trace("Successfully returned ad {}",advertisement.getId().toString());
            MDC.clear();
            return new ResponseEntity<>(advertisement, HttpStatus.OK);
        } else {
            NotFoundException notFoundException = new NotFoundException();
            logger.warn(notFoundException.getStackTrace().toString());
            MDC.clear();
            throw notFoundException;
        }
    }

    private void getAverageRating(Advertisement advertisement){
        ContactAverageReview contactAverageReview = new ContactAverageReview(reviewServiceClient);
        if (contactAverageReview != null && contactAverageReview.getAverage_rating(advertisement) != null){
            advertisement.setAverageContactRating(contactAverageReview.getAverage_rating(advertisement));
        } else {
            advertisement.setAverageContactRating(0);
        }
    }
}
