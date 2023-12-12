package com.sap.cc.bulletinboard.reviews;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceUrlProvider {

    //@Value("${service.reviews.url:}")
    private String serviceUrl = "https://bulletinboard-reviews-cdf.cfapps.eu10.hana.ondemand.com/api/v1/averageRatings/";

    public String getServiceUrl() {
        return serviceUrl;
    }
}
