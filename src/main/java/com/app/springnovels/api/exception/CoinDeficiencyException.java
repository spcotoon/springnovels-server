package com.app.springnovels.api.exception;

public class CoinDeficiencyException extends SpringNovelsException {

    private final static String message = "코인이 부족합니다.";

    public CoinDeficiencyException() {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
