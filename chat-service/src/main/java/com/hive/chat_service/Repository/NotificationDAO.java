package com.hive.chat_service.Repository;

import com.hive.Utility.NotificationType;
import com.hive.chat_service.Entity.Notification;
import com.hive.chat_service.Utility.TypeOfNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationDAO extends MongoRepository<Notification, String> {
    Page<Notification> findAllByRecipientId(Long recipientId, Pageable pageable);
    void deleteBySenderIdAndRecipientIdAndTypeOfNotification(Long senderId, Long recipientId, TypeOfNotification typeOfNotification);
    List<Notification> findBySenderIdAndRecipientIdAndTypeOfNotification(Long senderId, Long recipientId, TypeOfNotification typeOfNotification);

}