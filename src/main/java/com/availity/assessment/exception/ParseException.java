package com.availity.assessment.exception;

public class ParseException extends RuntimeException {
    private String msg;

    public ParseException(String msg) {
        super(msg);
        this.msg = msg;
    }
}
