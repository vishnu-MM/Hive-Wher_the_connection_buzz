package com.hive.postservice.Repository;

import com.hive.postservice.Entity.Post;
import com.hive.postservice.Utility.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface PostDAO extends JpaRepository<Post, Long> {
    @Query(value = "SELECT p FROM Post p ORDER BY RANDOM()")
    List<Post> findRandomPosts(Pageable pageable);
    List<Post> findByUserId(Long userId, Sort sort);
    List<Post> findUsersByDescriptionContainingIgnoreCase(String search);

    //When Post Type is All and createdOn
    Page<Post> findByCreatedOn(Timestamp createdOn, Pageable pageable);
    Page<Post> findByCreatedOnBetween(Timestamp startDate, Timestamp endDate, Pageable pageable);

    //When createdOn is All and Post Type
    Page<Post> findByPostType(PostType postType, Pageable pageable);

    //When createdOn and Post Type
    Page<Post> findByPostTypeAndCreatedOn(PostType postType, Timestamp createdOn, Pageable pageable);
    Page<Post> findByPostTypeAndCreatedOnBetween(PostType postType, Timestamp startDate, Timestamp endDate, Pageable pageable);
}