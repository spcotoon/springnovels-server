package com.app.springnovels.api.exception;

public class InvalidEmailException extends SpringNovelsException {
    private final static String message = "유효하지 않은 이메일 입니다.";

    public InvalidEmailException() {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return 422;
    }
}
