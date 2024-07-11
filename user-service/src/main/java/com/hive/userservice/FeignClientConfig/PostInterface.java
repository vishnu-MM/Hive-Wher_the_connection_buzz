package com.hive.userservice.FeignClientConfig;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("POST-SERVICE")
public interface PostInterface {
    @DeleteMapping("api/post/delete/by-user")
    public ResponseEntity<Void> deleteUserPosts(@RequestBody Long userId);
}
