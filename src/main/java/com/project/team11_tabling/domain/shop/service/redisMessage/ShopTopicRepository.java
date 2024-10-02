package com.project.team11_tabling.domain.shop.service.redisMessage;

import com.project.team11_tabling.domain.shop.service.redisMessage.RedisSubscriber;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j(topic = "topic repository")
public class ShopTopicRepository {
  private Map<String, ChannelTopic> topics = new HashMap<>();
  private final RedisMessageListenerContainer container;
  private Map<String, String> userSubscriptions = new ConcurrentHashMap<>();


  public void enterShopId(String shopId, String userId) {
    ChannelTopic topic;
    if (topics.get(shopId) == null) {
      log.info("topic 생성");
      topic = new ChannelTopic(shopId);
      topics.put(shopId, topic);
      initializeListeners(topic);
    }
    userSubscriptions.put(userId, shopId);
    log.info("userId {} is subscribed to shopId {}", userId, shopId);
  }

  public void leaveShopByUser(String shopId, String userId) {
    String shop = userSubscriptions.get(userId);
    if (shopId != null) {
      if (shop.isEmpty()) {
        userSubscriptions.remove(userId);
      }
      log.info("userId {} has unsubscribed from shopId {}", userId, shopId);
    }
  }

  public String getSubscribedRoomByUser(String userId) {
    return userSubscriptions.get(userId);
  }

  public ChannelTopic getTopic(String shopId){
    return topics.get(shopId);
  }

  public void initializeListeners(ChannelTopic topic) {
    log.info("initialize");
    MessageListenerAdapter listenerAdapter = new MessageListenerAdapter(redisSubscriber, "handleMessage");
    container.addMessageListener(listenerAdapter, topic);
  }
}
