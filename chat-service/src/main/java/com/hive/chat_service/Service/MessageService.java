package com.hive.chat_service.Service;

import com.hive.chat_service.Entity.Message;
import com.hive.chat_service.Repository.MessageDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageDAO dao;
    private final ChatRoomService chatRoomService;

    public Message save(Message chatMessage) {
        chatMessage.setTimestamp(Date.from(Instant.now()));
        var chatId = chatRoomService
                .getChatRoomId(chatMessage.getSenderId(), chatMessage.getRecipientId(), true)
                .orElseThrow();
        chatMessage.setChatId(chatId);
        dao.save(chatMessage);
        return chatMessage;
    }

    public List<Message> findMessages(String senderId, String recipientId) {
        var chatId = chatRoomService.getChatRoomId(senderId, recipientId, false);
        System.out.println("findMessages " + chatId);
        return chatId.map(dao::findByChatId)
                .orElse(new ArrayList<>());
    }
}