package com.hive.chat_service.Service;

import com.hive.chat_service.Entity.ChatRoom;
import com.hive.chat_service.Repository.ChatRoomDAO;
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

    public List<Long> getChatUsers(Long id) {
        String userId = String.valueOf(id);

        List<ChatRoom> chatRoomList = dao.findAllBySenderId(userId);
        chatRoomList.addAll( dao.findAllByRecipientId(userId) );


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
