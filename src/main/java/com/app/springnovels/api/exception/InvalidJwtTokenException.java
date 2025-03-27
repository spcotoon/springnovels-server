package com.app.springnovels.api.exception;

public class InvalidJwtTokenException extends SpringNovelsException{

    private static final String MESSAGE = "유효하지 않은 아이디 입니다.";

    public InvalidJwtTokenException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 401;
    }
}
