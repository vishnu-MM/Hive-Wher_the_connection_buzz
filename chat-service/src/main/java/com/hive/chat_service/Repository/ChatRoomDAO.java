package com.hive.chat_service.Repository;

import com.hive.chat_service.Entity.ChatRoom;
import com.hive.chat_service.Utility.MessageType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomDAO extends MongoRepository<ChatRoom, String> {
    Optional<ChatRoom> findBySenderIdAndRecipientId(String senderId, String recipientId);
    List<ChatRoom> findAllBySenderIdAndMessageType(String senderId, MessageType messageType);
    List<ChatRoom> findAllByRecipientIdAndMessageType(String recipientId, MessageType messageType);
}