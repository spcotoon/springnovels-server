package com.app.springnovels.api.exception;

public class InvalidSignInException extends SpringNovelsException {

    private final static String message = "아이디 또는 비밀번호가 올바르지 않습니다.";

    public InvalidSignInException() {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
