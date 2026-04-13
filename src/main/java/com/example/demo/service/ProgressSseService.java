package com.example.demo.service;

import com.example.demo.model.dto.ProgressUpdate;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class ProgressSseService {

  @Value("${progress.sse.service.timeout:5}")
  private int timeout;

  private static final Logger LOGGER = LoggerFactory.getLogger(ProgressSseService.class);
  private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
  private final String EVENT = "File processing progress";

  public SseEmitter subscribe(String id) {
    SseEmitter emitter = new SseEmitter(timeout * 60 * 1000L); // 5 minutes

    emitters.put(id, emitter);

    emitter.onCompletion(() -> emitters.remove(id, emitter));
    emitter.onTimeout(() -> emitters.remove(id, emitter));
    emitter.onError((e) -> emitters.remove(id, emitter));

    return emitter;
  }

  public void sendUpdate(UUID id, ProgressUpdate update) {
    String uuidStr = id.toString();
    SseEmitter emitter = emitters.get(uuidStr);
    if (emitter != null) {
      try {
        emitter.send(SseEmitter.event().name(EVENT).data(update));
      } catch (Exception e) {
        emitters.remove(uuidStr);
      }
    } else {
      LOGGER.warn("No SSE connection for user {}", uuidStr);
    }
  }

  private void complete(UUID id) {
    SseEmitter emitter = emitters.get(id.toString());
    if (emitter != null) {
      emitter.complete();
      emitters.remove(id.toString(), emitter);
    }
  }

  public void completeWithSuccess(UUID id) {
    sendUpdate(id, ProgressUpdate.createCompletedUpdate());
    complete(id);
  }
}
