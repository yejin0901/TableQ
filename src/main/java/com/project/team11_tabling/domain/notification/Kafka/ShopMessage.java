package com.project.team11_tabling.domain.notification.Kafka;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShopMessage {
  String shopId;
  String userId;
  String message;

  String event;


  public ShopMessage(String shopId, String userId, String text, String event){
    this.shopId = shopId;
    this.userId = userId;
    this.message = text;
    this.event = event;
  }

}
