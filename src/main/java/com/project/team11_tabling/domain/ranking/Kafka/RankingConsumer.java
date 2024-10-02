package com.project.team11_tabling.domain.ranking.Kafka;

import com.project.team11_tabling.domain.notification.Kafka.ShopMessage;

import java.util.HashMap;
import java.util.Map;

public class RankingConsumer {

    private final ShopRankingService shopRankingService;

    @KafkaListener(topics = "seat", groupId = "ranking-group")
    public void listenEntry2(String jsonMessage) {

        try {
            Map<String,String> info = ReadJsonMessage(jsonMessage);
            log.info("consumer group2 실행");
            String event = info.get("event");

            switch (event) {
                case "ENTER":
                    shopRankingService.incrementReservationCount(info.get("shopId"),"shops");
                    break;
                case "WAIT":
                    shopRankingService.incrementWaitCount(info.get("shopId"),"shops");
                    break;
                case "CANCEL":
                    shopRankingService.incrementCancelCount(info.get("shopId"),"shops");
                    shopRankingService.incrementCancelRate(info.get("shopId"),"shops");
                    break;
            }

        } catch (JsonProcessingException e) {
            e.getMessage();
        }

    }

    private Map<String,String> ReadJsonMessage(String jsonMessage) throws JsonProcessingException {
        Map<String,String> result = new HashMap<>(3);
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
}
