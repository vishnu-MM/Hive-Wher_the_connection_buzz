package com.hive.chat_service.Service;

import com.hive.DTO.Notification;
import com.hive.Utility.NotificationType;
import com.hive.chat_service.DTO.NotificationDTO;
import com.hive.chat_service.Repository.NotificationDAO;
import com.hive.chat_service.Utility.TypeOfNotification;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class MessageQueueService {
    private static final Logger log = LoggerFactory.getLogger(MessageQueueService.class);
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "notification", groupId = "notification-service")
    public void consumeEvents(Notification message) {
        log.info("Consumer consumed the events: {}", message.toString());

        String destination = "/queue/notification";
        NotificationDTO notification = notificationService.save(messageToDTO(message));
        messagingTemplate.convertAndSendToUser(
                String.valueOf(notification.getRecipientId()),
                destination,
                notification
        );
    }

    private NotificationDTO messageToDTO(Notification message) {
        TypeOfNotification typeOfNotification;
        if ( message.getNotificationType() == NotificationType.LIKE )
            typeOfNotification = TypeOfNotification.LIKE;
        else if ( message.getNotificationType() == NotificationType.COMMENT )
            typeOfNotification = TypeOfNotification.COMMENT;
        else if ( message.getNotificationType() == NotificationType.FRIEND_REQUEST )
            typeOfNotification = TypeOfNotification.FRIEND_REQUEST;
        else if ( message.getNotificationType() == NotificationType.FRIEND_REQUEST_ACCEPTED )
            typeOfNotification = TypeOfNotification.FRIEND_REQUEST_ACCEPTED;
        else
            throw new RuntimeException("Invalid NotificationType");

        return NotificationDTO.builder()
                .id(message.getId())
                .senderId(message.getSenderId())
                .recipientId(message.getRecipientId())
                .typeOfNotification(typeOfNotification)
                .date(Date.from(Instant.now()))
                .postId(message.getPostId())
                .commentId(message.getCommentId())
                .build();
    }

}
