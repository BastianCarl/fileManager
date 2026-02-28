package com.example.demo.exception;

public class FileServiceFailure extends RuntimeException {
    public FileServiceFailure(String message, Throwable cause) {
        super(message, cause);
    }
}