package com.example.demo.service;

import com.example.demo.model.dto.ProgressUpdate;
import java.util.Map;
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

  public SseEmitter subscribe(String uuid) {
    SseEmitter emitter = new SseEmitter(timeout * 60 * 1000L); // 5 minutes

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

  private void complete(String uuid) {
    SseEmitter emitter = emitters.get(uuid);
    if (emitter != null) {
      emitter.complete();
      emitters.remove(uuid, emitter);
    }
  }

  public void completeWithSuccess(String id) {
    sendUpdate(id, ProgressUpdate.createCompletedUpdate());
    complete(id);
  }
}
