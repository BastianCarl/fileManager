package com.example.demo.fileUploader.state;
import com.example.demo.model.Resource;

public interface State {
    State process(Resource resource);
}