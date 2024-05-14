package com.hive.postservice.Repository;

import com.hive.postservice.Entity.Like;
import com.hive.postservice.Entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeDAO extends JpaRepository<Like, Long> {
    Boolean existsByPostAndUserId(Post post, Long userId);
    List<Like> findByPost(Post post);
    Long countByPost(Post post);
}