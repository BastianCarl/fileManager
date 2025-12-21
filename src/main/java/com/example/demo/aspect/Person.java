package com.example.demo.aspect;

import org.springframework.stereotype.Component;

@Component
public class Person {

    public void work(int number) {
        System.err.println("I'm working really hard");
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {

        }

    }
}
