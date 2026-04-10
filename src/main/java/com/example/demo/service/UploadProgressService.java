package com.example.demo.service;

import com.example.demo.model.FileProcessingStep;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class UploadProgressService {

  private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

  public void registerEmitter(String id, SseEmitter emitter) {
    emitters.put(id, emitter);
  }

  public void removeEmitter(String id) {
    emitters.remove(id);
  }

  public void notifyProgress(String id, FileProcessingStep step) {
    SseEmitter emitter = emitters.get(id);
    if (emitter != null) {
      try {
        emitter.send(
            SseEmitter.event()
                .id(id)
                .name("progress")
                .data(Map.of("step", step.name(), "order", step.getOrder()))
                .build());
      } catch (IOException e) {
        removeEmitter(id);
      }
    }
  }

  public void notifyCompletion(String id, FileProcessingStep step) {
    notifyProgress(id, step);
    try {
      SseEmitter emitter = emitters.get(id);
      if (emitter != null) {
        emitter.send(
            SseEmitter.event().id(id).name("complete").data(Map.of("status", "success")).build());
        emitter.complete();
      }
    } catch (IOException e) {
      removeEmitter(id);
    }
  }

  public void notifyError(String id, String error) {
    try {
      SseEmitter emitter = emitters.get(id);
      if (emitter != null) {
        emitter.send(SseEmitter.event().id(id).name("error").data(Map.of("error", error)).build());
        emitter.complete();
      }
    } catch (IOException e) {
      removeEmitter(id);
    }
  }
}
