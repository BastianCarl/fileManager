package com.example.demo.stateMachine;

import com.example.demo.model.Resource;

public class DoneState implements State{
    @Override
    public boolean process(Resource resource) {
        return false;
    }
}
