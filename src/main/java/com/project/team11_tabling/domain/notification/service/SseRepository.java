package com.project.team11_tabling.domain.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Repository
@RequiredArgsConstructor
public class SseRepository {
  private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

  public void save(Long id, SseEmitter emitter) {
    emitters.put(id, emitter);
  }

  public void deleteById(Long userId) {
    emitters.remove(userId);
  }

  public SseEmitter get(Long userId) {
    return emitters.get(userId);
  }
}
