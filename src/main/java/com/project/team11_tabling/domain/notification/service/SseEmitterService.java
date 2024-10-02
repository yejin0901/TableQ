package com.project.team11_tabling.domain.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class SseEmitterService {
  private final SseRepository sseRepository;
  private static final Long DEFAULT_TIMEOUT = 600L * 1000 * 60;

  //SSE
  public void sendToClient(SseEmitter emitter, Object data) {
    try {
      emitter.send(data, MediaType.TEXT_PLAIN);
      log.info("Kafka로 부터 전달 받은 메세지 전송. message : {}", data);
    } catch (IOException e) {
      log.error("메시지 전송 에러 : {}", e.getMessage(), e);
      emitter.completeWithError(e);
    }
  }

  public SseEmitter addEmitter(Long userId){
    SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
    sseRepository.save(userId, emitter);
    log.info("emitterId " + emitter + "사용자 emitter 연결");

    emitter.onCompletion(() -> {
      log.info("onCompletion callback");
      emitter.complete();
    });
    emitter.onTimeout(() -> {
      log.info("onTimeout callback");
      emitter.complete();
    });
    return emitter;
  }

}
