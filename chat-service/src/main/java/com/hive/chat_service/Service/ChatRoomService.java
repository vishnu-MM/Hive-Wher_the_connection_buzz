package com.hive.chat_service.Service;

import com.hive.chat_service.Entity.ChatRoom;
import com.hive.chat_service.Repository.ChatRoomDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomDAO dao;
    
    public Optional<String> getChatRoomId( String senderId, String recipientId, boolean createNewRoomIfNotExists) {
        return dao.findBySenderIdAndRecipientId(senderId, recipientId)
                .map(ChatRoom::getChatId)
                .or(() -> {
                    if(createNewRoomIfNotExists) {
                        var chatId = createChatId(senderId, recipientId);
                        return Optional.of(chatId);
                    }
                    return  Optional.empty();
                });
    }

    private String createChatId(String senderId, String recipientId) {
        var chatId = String.format("%s_%s", senderId, recipientId);
        System.out.println(chatId +" sender " + senderId + " recipient " + recipientId );

        ChatRoom senderRecipient = ChatRoom
                .builder()
                .chatId(chatId)
                .senderId(senderId)
                .recipientId(recipientId)
                .build();

        ChatRoom recipientSender = ChatRoom
                .builder()
                .chatId(chatId)
                .senderId(recipientId)
                .recipientId(senderId)
                .build();

        dao.save(senderRecipient);
        dao.save(recipientSender);

        return chatId;
    }
}
