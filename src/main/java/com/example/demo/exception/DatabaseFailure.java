package com.example.demo.exception;

public class DatabaseFailure extends RuntimeException {
    public DatabaseFailure(String message, Throwable cause) {
        super(message, cause);
    }
}