package com.hive.adminservice.FeignConfig;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("POST-SERVICE")
public interface PostsInterface {
    @GetMapping("api/post/post-count")
    ResponseEntity<Long> getTotalPosts();

    @PutMapping("api/post/block-post")
    ResponseEntity<Void> blockPost(@RequestParam("postId") Long postId);

    @PutMapping("api/post/unblock-post")
    ResponseEntity<Void> unBlockPost(@RequestParam("postId") Long postId);

    @PutMapping("api/post/block-comment")
    ResponseEntity<Void> blockComment(@RequestParam("commentId") Long commentId);

    @PutMapping("api/post/unblock-comment")
    ResponseEntity<Void> unBlockComment(@RequestParam("commentId") Long commentId);

}
