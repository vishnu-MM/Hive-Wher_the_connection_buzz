package com.hive.chat_service.DTO;

import com.hive.chat_service.Utility.MessageType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageDTO {
    private Long sender;
    private String content;
    private MessageType type;
}