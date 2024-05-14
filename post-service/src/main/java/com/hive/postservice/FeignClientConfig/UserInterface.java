package com.hive.postservice.FeignClientConfig;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("USER-SERVICE")
public interface UserInterface {
    @GetMapping("api/user/exists-profile/{id}")
    public ResponseEntity<Boolean> isUserExists(@PathVariable Long id);
}