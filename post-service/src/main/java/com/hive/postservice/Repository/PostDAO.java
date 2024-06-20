package com.hive.postservice.Repository;

import com.hive.postservice.Entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostDAO extends JpaRepository<Post, Long> {
    @Query(value = "SELECT p FROM Post p ORDER BY RANDOM()")
    List<Post> findRandomPosts(Pageable pageable);
    List<Post> findByUserId(Long userId);
    List<Post> findUsersByDescriptionContaining(String search);
}