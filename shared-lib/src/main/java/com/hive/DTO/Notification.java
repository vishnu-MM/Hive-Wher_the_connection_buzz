package com.hive.DTO;

import com.hive.Utility.NotificationType;
import java.time.Instant;

public class Notification {
    private String id;
    private Long senderId;
    private Long recipientId;
    private NotificationType notificationType;
    private Long postId;
    private Long commentId;

    public Notification() {}
    public Notification( String id, Long senderId,
                         Long recipientId, NotificationType NotificationType,
                         Long postId, Long commentId ) {
        this.id = id;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.notificationType = NotificationType;
        this.postId = postId;
        this.commentId = commentId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public Long getRecipientId() { return recipientId; }
    public void setRecipientId(Long recipientId) { this.recipientId = recipientId; }

    public NotificationType getNotificationType() { return notificationType; }
    public void setNotificationType( NotificationType notificationType ) {
        this.notificationType = notificationType;
    }

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public Long getCommentId() { return commentId; }
    public void setCommentId(Long commentId) { this.commentId = commentId; }

    @Override
    public String toString() {
        return "Notification{" +
                "id='" + id + '\'' +
                ", senderId=" + senderId +
                ", recipientId=" + recipientId +
                ", NotificationType=" + notificationType +
                ", postId=" + postId +
                ", commentId=" + commentId +
                '}';
    }
}