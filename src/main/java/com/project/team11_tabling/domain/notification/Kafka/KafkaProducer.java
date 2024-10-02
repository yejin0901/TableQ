package com.project.team11_tabling.domain.notification.Kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class KafkaProducer {
  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final ObjectMapper objectMapper;

  public void sendWaitingMessage(String shopId, String userId, String text) {
    String jsonMessage;
    ShopMessage message = new ShopMessage(shopId, userId, text,"WAIT");
      try {
      jsonMessage = objectMapper.writeValueAsString(message);
      System.out.println("send message");
      kafkaTemplate.send("seat", userId, jsonMessage);
    } catch(JsonProcessingException e){
      e.getMessage();
    }
  }

  public void sendCancelMessage(String shopId, String userId, String text) {
    String jsonMessage;
    ShopMessage message = new ShopMessage(shopId, userId, text, "CANCEL");
      try {
      jsonMessage = objectMapper.writeValueAsString(message);
      System.out.println("send message");
      kafkaTemplate.send("seat", userId, jsonMessage);
    } catch(JsonProcessingException e){
      e.getMessage();
    }
  }

  public void sendEnterMessage(String shopId, String userId, String text) {
    String jsonMessage;
    ShopMessage message = new ShopMessage(shopId, userId, text,"ENTER");
      try {
      jsonMessage = objectMapper.writeValueAsString(message);
      System.out.println("send message");
      kafkaTemplate.send("seat", userId, jsonMessage);
    } catch(JsonProcessingException e){
      e.getMessage();
    }
  }


}
