package com.hive.chat_service.Service;

import com.hive.Utility.NotificationType;
import com.hive.chat_service.DTO.NotificationDTO;
import com.hive.chat_service.DTO.PaginationDTO;
import com.hive.chat_service.Entity.Notification;
import com.hive.chat_service.Repository.NotificationDAO;
import com.hive.chat_service.Utility.TypeOfNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationDAO dao;

    public NotificationDTO save(NotificationDTO notificationDTO) {
        if (notificationDTO.getTypeOfNotification() == TypeOfNotification.FRIEND_REQUEST_ACCEPTED) {
            deleteFriendRequestNotification(notificationDTO.getSenderId(), notificationDTO.getRecipientId());
        }
        Notification notification = Notification.builder()
                .senderId(notificationDTO.getSenderId())
                .recipientId(notificationDTO.getRecipientId())
                .typeOfNotification(notificationDTO.getTypeOfNotification())
                .date(Date.from(Instant.now()))
                .postId(notificationDTO.getPostId())
                .commentId(notificationDTO.getCommentId())
                .build();
        return entityToDTO(dao.save(notification));
    }

    private void deleteFriendRequestNotification(Long senderId, Long recipientId) {
        dao.deleteBySenderIdAndRecipientIdAndTypeOfNotification(recipientId, senderId, TypeOfNotification.FRIEND_REQUEST);
    }

    public PaginationDTO findAll(Long userId, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("id").descending());
        Page<Notification> page = dao.findAllByRecipientId(userId, pageable);

        List<NotificationDTO> contents = page
                .getContent()
                .stream()
                .map(this::entityToDTO)
                .toList();
        return PaginationDTO.builder()
                .contents(contents)
                .pageNo(page.getNumber())
                .pageSize(page.getSize())
                .hasNext(page.hasNext())
                .isLast(page.isLast())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    private NotificationDTO entityToDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .senderId(notification.getSenderId())
                .recipientId(notification.getRecipientId())
                .typeOfNotification(notification.getTypeOfNotification())
                .date(notification.getDate())
                .postId(notification.getPostId())
                .commentId(notification.getCommentId())
                .build();
    }
}