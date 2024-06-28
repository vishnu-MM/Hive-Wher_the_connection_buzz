package com.hive.userservice.Service;

import com.hive.DTO.Notification;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class MessageQueueService {
    private static final Logger log = LoggerFactory.getLogger(MessageQueueService.class);
    private final KafkaTemplate<String,Object> template;

    public void sendMessageToTopic(String topic, Notification message) {
        try {
            CompletableFuture<SendResult<String, Object>> response = template.send(topic, message);

            response.whenComplete((result, ex) -> {
                if (ex == null) {
                    System.out.println("Sent message=[" + message + "] with offset=[" + result.getRecordMetadata().offset() + "]");
                } else {
                    System.out.println("Unable to send message=[" + message + "] due to : " + ex.getMessage());
                }
            });
        }
        catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}