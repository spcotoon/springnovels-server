package com.app.springnovels.api.exception;

public class UserNotLoginException extends SpringNovelsException {
    private final static String message = "로그인이 필요합니다.";


    public UserNotLoginException() {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return 401;
    }
}
