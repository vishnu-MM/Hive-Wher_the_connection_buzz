package com.hive.chat_service.Service;

import com.hive.chat_service.Entity.ChatRoom;
import com.hive.chat_service.Repository.ChatRoomDAO;
import com.hive.chat_service.Utility.MessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomDAO dao;
    
    public Optional<String> getChatRoomId( String senderId, String recipientId,
                                           MessageType messageType, boolean createNewRoomIfNotExists) {
        return dao.findBySenderIdAndRecipientId(senderId, recipientId)
                .map(ChatRoom::getChatId)
                .or(() -> {
                    if(createNewRoomIfNotExists) {
                        var chatId = createChatId(senderId, recipientId, messageType);
                        return Optional.of(chatId);
                    }
                    return  Optional.empty();
                });
    }

    private String createChatId(String senderId, String recipientId, MessageType messageType) {
        if ( messageType == MessageType.GROUP) {
            ChatRoom group = ChatRoom
                    .builder()
                    .chatId(recipientId)
                    .senderId(senderId)
                    .recipientId(recipientId)
                    .messageType(messageType)
                    .build();
            dao.save(group);
            return recipientId;
        }

        var chatId = String.format("%s_%s", senderId, recipientId);
        System.out.println(chatId +" sender " + senderId + " recipient " + recipientId );


        ChatRoom senderRecipient = ChatRoom
                .builder()
                .chatId(chatId)
                .senderId(senderId)
                .recipientId(recipientId)
                .messageType(messageType)
                .build();

        ChatRoom recipientSender = ChatRoom
                .builder()
                .chatId(chatId)
                .senderId(recipientId)
                .recipientId(senderId)
                .messageType(messageType)
                .build();

        dao.save(senderRecipient);
        dao.save(recipientSender);

        return chatId;
    }

    public List<Long> getChatUsers(Long id) {
        String userId = String.valueOf(id);

        List<ChatRoom> chatRoomList = dao.findAllBySenderIdAndMessageType(userId, MessageType.PRIVATE);
        chatRoomList.addAll( dao.findAllByRecipientIdAndMessageType(userId, MessageType.PRIVATE) );


        Set<Long> userIdSet = chatRoomList.stream()
                                .map(chatRoom -> chatRoomToUserId(chatRoom, userId))
                                .collect(Collectors.toSet());
        return new ArrayList<>(userIdSet);
    }

    private Long chatRoomToUserId(ChatRoom chatRoom, String userId) {
        if (chatRoom.getSenderId().equals(userId)) {
            return Long.parseLong(chatRoom.getRecipientId());
        } else {
            return Long.parseLong(chatRoom.getSenderId());
        }
    }
}
