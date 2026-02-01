package com.example.demo.exception;


public class DuplicatedFile extends Exception {
    public DuplicatedFile(String message) {
        super(message);
    }
}