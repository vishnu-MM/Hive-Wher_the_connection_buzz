package com.hive.adminservice.Service;

public interface AdminService {
    Long getTotalUsers();
    Long getTotalPosts();
    void blockUser(Long userId, String reason);
    void unBlockUser(Long userId);
    void blockPost(Long post);
    void unBlockPost(Long post);
    void blockComment(Long comment);
    void unBlockComment(Long comment);
}