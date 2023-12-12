package com.sap.bulletinboard.ads.controllers.dto;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("error")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public class ErrorDto {
    public int status;
    public String message; // user-facing (localizable) message, describing the error
    public String target; // endpoint of origin request
    public List<DetailError> details;

    public ErrorDto(HttpStatus status, String message, WebRequest request, DetailError... errors) {
        this.status = status.value();
        this.message = message;
        if (message == null) {
            this.message = status.getReasonPhrase();
        }
        this.details = Arrays.asList(errors);
        this.target = request.getDescription(false).substring(4);
    }

    public static class DetailError {
        public final String message; // user-facing (localizable) message, describing the error

        public DetailError(String message) {
            this.message = message;
        }
    }
}