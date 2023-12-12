package com.sap.bulletinboard.ads.controllers.dto;

import org.springframework.data.domain.Page;

public class PageHeaderBuilder {

    public static String createLinkHeaderString(Page<?> page, String path) {
        String linkHeader = "";
        int pageNumber = page.getNumber();
        if (page.hasPrevious()) {
            linkHeader += "<" + path + (pageNumber - 1) + ">; rel=\"previous\"";
        }
        if (page.hasNext()) {
            if (linkHeader.length() > 0) {
                linkHeader += ", ";
            }
            linkHeader += "<" + path + (pageNumber + 1) + ">; rel=\"next\"";
        }
        return linkHeader;
    }
}