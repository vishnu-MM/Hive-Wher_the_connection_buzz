package com.hive.chat_service.DTO;

import com.hive.chat_service.Utility.TypeOfNotification;
import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class NotificationDTO {
    private String id;
    private Long senderId;
    private Long recipientId;
    private TypeOfNotification typeOfNotification;
    private Instant timestamp;
    private Long postId;
    private Long commentId;
}