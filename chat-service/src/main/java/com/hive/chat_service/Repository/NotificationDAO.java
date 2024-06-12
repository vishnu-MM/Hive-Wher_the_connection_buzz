package com.hive.chat_service.Repository;

import com.hive.chat_service.Entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationDAO extends MongoRepository<Notification, String> {
    Page<Notification> findAllByRecipientId(Long recipientId, Pageable pageable);
}