package com.iskandev.rdxcalc.exceptions;

public final class TooLargeNumberException extends Exception {

    private final String message = "!TOO LARGE NUMBER!";

    @Override
    public String getMessage() {
        return message;
    }
}