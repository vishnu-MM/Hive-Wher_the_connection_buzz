package com.hive.chat_service.Entity;

import com.hive.chat_service.Utility.MessageType;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
public class ChatRoom {
    @Id private String id;
    private String chatId;
    private String senderId;
    private String recipientId;
    private MessageType messageType;
}