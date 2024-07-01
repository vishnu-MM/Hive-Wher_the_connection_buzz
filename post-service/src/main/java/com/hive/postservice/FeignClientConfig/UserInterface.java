package com.hive.postservice.FeignClientConfig;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("USER-SERVICE")
public interface UserInterface {
    @GetMapping("api/user/exists-profile/{id}")
    public ResponseEntity<Boolean> isUserExists(@PathVariable Long id);

    @GetMapping("api/user/friends-ids")
    ResponseEntity<List<Long>> getUserFriendsIds(@RequestParam("userId") Long userId);
}