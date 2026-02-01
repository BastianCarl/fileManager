package com.example.demo.exception;

public class FailedRestoreProcedure extends RuntimeException {
    public FailedRestoreProcedure(String message) {
        super(message);
    }
}