package com.example.demo.fileState;
import com.example.demo.model.Resource;

public interface State {
    State process(Resource resource);
}