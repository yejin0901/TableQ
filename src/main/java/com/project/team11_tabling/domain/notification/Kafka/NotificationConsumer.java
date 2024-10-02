package com.project.team11_tabling.domain.notification.Kafka;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.team11_tabling.domain.booking.repository.BookingRepository;
import com.project.team11_tabling.domain.wish.repository.WishRepository;
import com.project.team11_tabling.domain.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.project.team11_tabling.domain.booking.service.WaitingQueueService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Component
public class NotificationConsumer {

  private final ObjectMapper objectMapper;
  private final BookingRepository waitingListRepository;
  private final ShopRepository shopRepository;
  private final SseRepository sseRepository;
  private final SseEmitterService sseEmitterService;
  private final WaitingQueueService waitingQueueService;


  @KafkaListener(topics = "seat", groupId = "notification-group")
  public void listenNotification(String jsonMessage) {

    try {
      Map<String,String> info = ReadJsonMessage(jsonMessage);
      //SSE
      SseEmitter emitter = sseRepository.get(Long.parseLong(info.get("userId")));
      String event = info.get("event");

      switch (event) {
        case "ENTER":
          waitingQueueService.popQueue(info.get("shopId"));
          break;
        case "WAIT":
          waitingQueueService.addQueue(info.get("shopId"),info.get("userId"));
          break;
        case "CANCEL":
          waitingQueueService.removeQueue(info.get("shopId"),info.get("userId"));
          break;
      }

      log.info(info.get("message"));
      sseEmitterService.sendToClient(emitter, info.get("message"));
    } catch (JsonProcessingException e) {
      e.getMessage();
    }

  }


  private Map<String,String> ReadJsonMessage(String jsonMessage) throws JsonProcessingException {
    Map<String,String> result = new HashMap<>(4);
    ShopMessage message;
    message = objectMapper.readValue(jsonMessage, ShopMessage.class);
    Long shopId = Long.valueOf(message.getShopId());
    Long userId = Long.valueOf(message.getUserId());
    String text = message.getMessage();
    String event = message.getEvent();

    Shop shop = getShop(shopId);
    String notifications = userId +"님 " + shop.getName()+ text;

    result.put("userId", String.valueOf(userId));
    result.put("shopId", String.valueOf(shopId));
    result.put("message", notifications);
    result.put("event", event);

    return result;
  }

  private Shop getShop(Long shopId){
    return shopRepository.findById(shopId).orElseThrow(() -> new IllegalArgumentException("식당 정보가 없습니다."));
  }
}
