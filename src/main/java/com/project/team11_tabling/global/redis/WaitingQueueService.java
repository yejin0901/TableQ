package com.project.team11_tabling.global.redis;


import com.project.team11_tabling.domain.shop.service.redisMessage.RealtimeWaitingDataService;

import com.project.team11_tabling.global.event.CallingEvent;
import com.project.team11_tabling.global.event.CancelEvent;
import com.project.team11_tabling.global.event.DoneEvent;
import com.project.team11_tabling.global.event.WaitingEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Slf4j(topic = "WaitingQueueService")
@Component
public class WaitingQueueService {

  private final RedisTemplate<String, String> redisTemplate;
  private final RedissonClient redissonClient;
  private final RealtimeWaitingDataService realtimeWaitingDataService;

  public WaitingQueue(RedissonClient redissonClient, RedisTemplate<String, String> redisTemplate) {
    this.redissonClient = redissonClient;
    this.redisTemplate = redisTemplate;
  }

  public Long addQueue(String shopId, String customerId) {
    String lockName = "lock:shop:" + shopId;
    RLock lock = redissonClient.getLock(lockName);
    Long position = null;
    try {
      boolean isLocked = lock.tryLock(10, 60, TimeUnit.SECONDS);
      if (isLocked) {
        // 락 획득
        ListOperations<String, String> listOps = redisTemplate.opsForList();
        position = listOps.leftPush(shopId, customerId);
        System.out.println("add queue");
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } finally {
      lock.unlock();
    }
    return position;
  }

  public String popQueue(String shopId) {
    String lockName = "lock:shop:" + shopId;
    RLock lock = redissonClient.getLock(lockName);
    String customerId = null;
    try {
      boolean isLocked = lock.tryLock(10, 60, TimeUnit.SECONDS);
      if (isLocked) {
        // 락 획득
        ListOperations<String, String> listOps = redisTemplate.opsForList();
        customerId = listOps.rightPop(shopId);
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } finally {
      lock.unlock();
    }
    return customerId;
  }

  public Long queueSize(String storeId){
    ListOperations<String, String> listOps = redisTemplate.opsForList();
    return listOps.size(storeId);
  }

  public List<String> fetchAllKeys() {
    List<String> keysList = new ArrayList<>();
    redisTemplate.execute((RedisConnection connection) -> {
      Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().match("*").count(1000).build());
      while (cursor.hasNext()) {
        keysList.add(new String(cursor.next()));
      }
      return null;
    });
    return keysList;
  }


  public void sendJsonMessage(Long shopId, String status){

    Long size = getWaitingQueueSize(shopId);
    log.info("queue size : " + size);
    realtimeWaitingDataService.sendUpdate(shopId, size, status);
  }


}
