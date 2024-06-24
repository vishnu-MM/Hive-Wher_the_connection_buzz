package com.hive.adminservice.Service;

import com.hive.adminservice.FeignConfig.PostsInterface;
import com.hive.adminservice.FeignConfig.UserInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final PostsInterface postsInterface;
    private final UserInterface userInterface;

    @Override
    public Long getTotalUsers() {
        return userInterface.getTotalUsers().getBody();
    }

    @Override
    public Long getTotalPosts() {
        return postsInterface.getTotalPosts().getBody();
    }

    @Override
    public void blockUser(Long userId, String reason) {
        userInterface.blockUser(userId, reason);
    }

    @Override
    public void unBlockUser(Long userId) {
        userInterface.unBlockUser(userId);
    }

    @Override
    public void blockPost(Long post) {
        postsInterface.blockPost(post);
    }

    @Override
    public void unBlockPost(Long post) {
        postsInterface.unBlockPost(post);
    }

    @Override
    public void blockComment(Long comment) {
        postsInterface.blockComment(comment);
    }

    @Override
    public void unBlockComment(Long comment) {
        postsInterface.unBlockComment(comment);
    }
}