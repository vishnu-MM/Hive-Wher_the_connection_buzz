package com.hive.chat_service.Repository;

import com.hive.chat_service.Entity.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface MessageDAO extends MongoRepository<Message, String> {
    List<Message> findByChatId(String chatId);
}