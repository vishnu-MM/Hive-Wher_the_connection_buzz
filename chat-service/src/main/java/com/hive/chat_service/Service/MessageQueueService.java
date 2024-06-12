package com.hive.chat_service.Service;

import com.hive.DTO.Notification;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class MessageQueueService {

    @KafkaListener(topics = "like-notification", groupId = "notification-service")
    public void consumeEvents(Notification notification) {
        System.out.println("Consumer consumed the events: " + notification.toString());
    }
}
