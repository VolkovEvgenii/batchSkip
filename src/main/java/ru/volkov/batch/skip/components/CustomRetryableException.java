package ru.volkov.batch.skip.components;

public class CustomRetryableException extends Exception {

    public CustomRetryableException() {
        super();
    }

    public CustomRetryableException(String msg) {
        super(msg);
    }
}
