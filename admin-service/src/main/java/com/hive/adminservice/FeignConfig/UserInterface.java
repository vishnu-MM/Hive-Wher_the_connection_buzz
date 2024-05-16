package com.hive.adminservice.FeignConfig;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("USER-SERVICE")
public interface UserInterface {

    @GetMapping("api/user/user-count")
    public ResponseEntity<Long> getTotalUsers();

    @PutMapping("api/user/block-user")
    public ResponseEntity<Void> blockUser(@RequestParam("userId") Long userId);

    @PutMapping("api/user/unblock-user")
    public ResponseEntity<Void> unBlockUser(@RequestParam("userId") Long userId);
}