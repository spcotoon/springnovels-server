package com.app.springnovels.api.exception;

public class NotEnoughCoinException extends SpringNovelsException {
    private final static String message = "코인이 부족합니다.";


    public NotEnoughCoinException() {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
