package com.app.springnovels.api.exception;

public class NotExistMemberException extends SpringNovelsException {
    private final static String message = "존재하지 않는 아이디 입니다.";


    public NotExistMemberException() {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
