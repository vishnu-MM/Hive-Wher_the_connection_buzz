package com.hive.postservice.Repository;

import com.hive.postservice.Entity.Comment;
import com.hive.postservice.Entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentDAO extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);
    Long countAllByPost(Post post);
}