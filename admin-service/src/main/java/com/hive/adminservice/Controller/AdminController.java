package com.hive.adminservice.Controller;

import com.hive.adminservice.Service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService service;
    
    @GetMapping("user-count")
    public ResponseEntity<Long> getTotalUsers(){
        return ResponseEntity.ok(service.getTotalUsers());
    }
    
    @GetMapping("post-count")
    public ResponseEntity<Long> getTotalPosts(){
        return ResponseEntity.ok(service.getTotalPosts());
    }
    
    @PutMapping("block-user")
    @ResponseStatus(HttpStatus.OK)
    public void blockUser(@RequestParam("userId") Long userId){
        service.blockUser(userId);
    }

    @PutMapping("block-post")
    @ResponseStatus(HttpStatus.OK)
    public void blockPost(@RequestParam("postId") Long postId){
        service.blockPost(postId);
    }

    @PutMapping("block-comment")
    @ResponseStatus(HttpStatus.OK)
    public void blockComment(@RequestParam("commentId") Long commentId){
        service.blockComment(commentId);
    }    
    
    @PutMapping("unblock-user")
    @ResponseStatus(HttpStatus.OK)
    public void unBlockUser(@RequestParam("userId") Long userId){
        service.unBlockUser(userId);
    }

    @PutMapping("unblock-post")
    @ResponseStatus(HttpStatus.OK)
    public void unBlockPost(@RequestParam("postId") Long postId){
        service.unBlockPost(postId);
    }

    @PutMapping("unblock-comment")
    @ResponseStatus(HttpStatus.OK)
    public void unBlockComment(@RequestParam("commentId") Long commentId){
        service.unBlockComment(commentId);
    }
}