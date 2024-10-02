package com.project.team11_tabling.domain.notification.service;

import com.team10.temp.domain.Notification.Kafka.kafkaProducer;
import com.team10.temp.domain.shopSeatInfo.ShopSeatInfoRepository;
import com.team10.temp.domain.waiting.WaitingQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.project.team11_tabling.domain.notification.Kafka.KafkaProducer;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationMessageService {
  private final WaitingQueue waitingQueue;
  private final ShopSeatInfoRepository shopSeatInfoRepository;
  private final KafkaProducer producer;

  /** 알림종류
   * 1. cancel 대기취소
   * 2. waiting 입장대기
   * 3. enter 가게입장
   */
  public void SendCancelMessage(Long shopId, Long userId){
    producer.sendCancelMessage(shopId.toString(), userId.toString(), "대기 취소되었습니다");
  }

  public void SendWaitingMessage(Long shopId, Long userId){
    producer.sendWaitingMessage(shopId.toString(), userId.toString(), "대기 등록되었습니다");
  }

  public void SendEnterMessage(Long shopId, Long userId){
    List<String> keyList = waitingQueue.fetchAllKeys();
    log.info("대기 식당 수 : " + keyList.size());
    for(String key:keyList){
      log.info("kafka enter message를 보냄");
      String userId = waitingQueue.popQueue(key);
      //대기열 취소 id
      if(userId.equals("0")) continue;
      if(waitingQueue.queueSize(key)==0){
        waitingQueue.deleteKey(key); //key 삭제, 대기열 큐 삭제
      }
      producer.sendEnterMessage(key, userId, "가게 입장해주세요");
    }
  }
}
