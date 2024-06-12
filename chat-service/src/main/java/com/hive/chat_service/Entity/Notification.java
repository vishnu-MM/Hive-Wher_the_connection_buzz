package com.hive.chat_service.Entity;

import com.hive.chat_service.Utility.TypeOfNotification;
import com.mongodb.lang.Nullable;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Data
@Builder
@Document
public class Notification {
    @Id private String id;
    private Long senderId;
    private Long recipientId;
    private TypeOfNotification typeOfNotification;
    private Date date;
    @Nullable private Long postId;
    @Nullable private Long commentId;
}