package com.example.demo.service;

import com.example.demo.model.dto.ProgressUpdate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class ProgressSseService {

  private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
  private final String EVENT = "progress";

  public SseEmitter subscribe(String uuid) {
    SseEmitter emitter = new SseEmitter(0L); // fără timeout

    emitters.put(uuid, emitter);

    emitter.onCompletion(() -> emitters.remove(uuid));
    emitter.onTimeout(() -> emitters.remove(uuid));
    emitter.onError((e) -> emitters.remove(uuid));

    return emitter;
  }

  public void sendUpdate(String uuid, ProgressUpdate update) {
    SseEmitter emitter = emitters.get(uuid);
    if (emitter != null) {
      try {
        emitter.send(SseEmitter.event().name(EVENT).data(update));
      } catch (Exception e) {
        emitters.remove(uuid);
      }
    }
  }
}
