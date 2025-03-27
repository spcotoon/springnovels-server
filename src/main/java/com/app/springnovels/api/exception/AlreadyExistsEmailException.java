package com.app.springnovels.api.exception;

public class AlreadyExistsEmailException extends SpringNovelsException{

    private final static String message = "이미 사용중인 이메일 입니다.";

    public AlreadyExistsEmailException() {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
