package com.hive.chat_service.Service;

import com.hive.chat_service.Entity.Message;
import com.hive.chat_service.Repository.MessageDAO;
import com.hive.chat_service.Utility.MessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageDAO dao;
    private final ChatRoomService chatRoomService;

    public Message save(Message chatMessage) {
        chatMessage.setId(null);
        chatMessage.setTimestamp(String.valueOf(Instant.now()));
        var chatId = chatRoomService
                .getChatRoomId(chatMessage.getSenderId(), chatMessage.getRecipientId(), chatMessage.getMessageType(), true)
                .orElseThrow(() -> new RuntimeException("Couldn't get chat id"));
        chatMessage.setChatId(chatId);
        return dao.save(chatMessage);
    }

    public List<Message> findMessages(String senderId, String recipientId, MessageType messageType) {
        String chatId = recipientId;
        if (messageType == MessageType.PRIVATE) {
            var chatIdOpt = chatRoomService.getChatRoomId(senderId, recipientId, messageType, false);
            if (chatIdOpt.isEmpty()) return new ArrayList<>();
            chatId = chatIdOpt.get();
        }
        return dao.findByChatId(chatId);
    }
}