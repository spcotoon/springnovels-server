package com.app.springnovels.api.exception;

import java.util.HashMap;
import java.util.Map;

public abstract class SpringNovelsException extends RuntimeException{

    private final Map<String, String> validation = new HashMap<>();

    public SpringNovelsException(String message) {
        super(message);
    }

    public SpringNovelsException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract int getStatusCode();

    public void addValidation(String fieldName, String message) {
        validation.put(fieldName, message);
    }
}
