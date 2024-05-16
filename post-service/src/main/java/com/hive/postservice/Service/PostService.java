package com.hive.postservice.Service;

import com.hive.postservice.DTO.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PostService {
    PostDTO createPost(MultipartFile file, PostRequestDTO postRequestDTO);
    byte[] getPostFile(Long postId) throws IOException;
    PostDTO getPost(Long postId);
    List<PostDTO> getPostsForUser(Long userId);
    List<PostDTO> getRandomPosts(Integer pageNumber, Integer pageSize);
    void deletePost(Long postId);
    PostDTO blockPost(Long postId);
    PostDTO unBlockPost(Long postId);
    Long postCount();

    CommentDTO createComment(CommentRequestDTO commentRequest);
    void deleteComment(Long commentId);
    List<CommentDTO> getCommentsForPost(Long postId);
    CommentDTO getComment(Long commentId);
    Long commentCount(Long postId);
    CommentDTO blockComment(Long commentId);
    CommentDTO unBlockComment(Long commentId);

    LikeDTO createLike(LikeRequestDTO likeRequest);
    LikeDTO getLike(Long likeId);
    List<LikeDTO> getLikesForPost(Long postId);
    void deleteLike(Long likeId);
    Long likeCount(Long postId);

}