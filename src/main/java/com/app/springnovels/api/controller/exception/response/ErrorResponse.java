package com.app.springnovels.api.controller.exception.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ErrorResponse {

    private final String code;
    private final String message;
    private final List<ValidationTuple> validation = new ArrayList<>();

    @Builder
    private ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public void addValidation(String fieldName, String message) {
        this.validation.add(new ValidationTuple(fieldName, message));
    }

    @RequiredArgsConstructor
    @Getter
    private class ValidationTuple {
        private final String fieldName;
        private final String errorMessage;
    }
}
