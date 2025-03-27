package com.app.springnovels.api.exception;

public class NotExistNovelException extends SpringNovelsException {

    private final static String message = "존재하지 않는 소설입니다.";


    public NotExistNovelException() {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
