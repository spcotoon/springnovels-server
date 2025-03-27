package com.app.springnovels.api.exception;

public class AlreadyExistsNicknameException extends SpringNovelsException{
    private final static String message = "이미 사용중인 닉네임 입니다.";

    public AlreadyExistsNicknameException() {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
