package com.example.demo.service;

import com.example.demo.model.dto.ProgressUpdate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class ProgressSseService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProgressSseService.class);
  private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
  private final String EVENT = "progress";

  public SseEmitter subscribe(String uuid) {
    SseEmitter emitter = new SseEmitter(5 * 60 * 1000L); // 5 minute

    emitters.put(uuid, emitter);

    emitter.onCompletion(() -> emitters.remove(uuid, emitter));
    emitter.onTimeout(() -> emitters.remove(uuid, emitter));
    emitter.onError((e) -> emitters.remove(uuid, emitter));

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
    } else {
      LOGGER.warn("No SSE connection for user {}", uuid);
    }
  }

  public void complete(String uuid) {
    SseEmitter emitter = emitters.get(uuid);
    if (emitter != null) {
      emitter.complete();
      emitters.remove(uuid, emitter);
    }
  }
}
