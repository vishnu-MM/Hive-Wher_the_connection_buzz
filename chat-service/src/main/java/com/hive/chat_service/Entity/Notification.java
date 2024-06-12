package com.hive.chat_service.Entity;

import com.hive.chat_service.Utility.TypeOfNotification;
import com.mongodb.lang.Nullable;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Data
@Builder
@Document
public class Notification {
    @Id private String id;
    private Long senderId;
    private Long recipientId;
    private TypeOfNotification typeOfNotification;
    private Instant timestamp;
    @Nullable private Long postId;
    @Nullable private Long commentId;
}