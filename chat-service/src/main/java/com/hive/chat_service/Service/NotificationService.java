package com.hive.chat_service.Service;

import com.hive.chat_service.DTO.NotificationDTO;
import com.hive.chat_service.DTO.PaginationDTO;
import com.hive.chat_service.Entity.Notification;
import com.hive.chat_service.Repository.NotificationDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationDAO dao;

    public NotificationDTO save(NotificationDTO notificationDTO) {
        Notification notification = Notification.builder()
                .senderId(notificationDTO.getSenderId())
                .recipientId(notificationDTO.getRecipientId())
                .typeOfNotification(notificationDTO.getTypeOfNotification())
                .timestamp(Instant.now())
                .postId(notificationDTO.getPostId())
                .commentId(notificationDTO.getCommentId())
                .build();
        return entityToDTO(dao.save(notification));
    }

    public PaginationDTO findAll(Long userId, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("timestamp").descending());
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
                .timestamp(notification.getTimestamp())
                .postId(notification.getPostId())
                .commentId(notification.getCommentId())
                .build();
    }
}