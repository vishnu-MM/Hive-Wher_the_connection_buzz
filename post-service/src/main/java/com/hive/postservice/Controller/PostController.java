package com.hive.postservice.Controller;

import com.hive.postservice.DTO.*;
import com.hive.postservice.Service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService service;

    @PostMapping("create")
    public ResponseEntity<PostDTO> createPost(@RequestPart("file") MultipartFile file,
                                              @RequestPart("post") PostRequestDTO postRequestDTO){
        return new ResponseEntity<>( service.createPost(file, postRequestDTO), HttpStatus.CREATED);
    }

    @GetMapping("single-post")
    public ResponseEntity<PostDTO> getPost(@RequestParam("postId") Long postId){
        return new ResponseEntity<>(service.getPost(postId), HttpStatus.OK);
    }

    @GetMapping("/files/{postId}")
    public ResponseEntity<byte[]> getImage(@PathVariable("postId") Long postId ) {
        try {
            byte[] imageBytes = service.getPostFile(postId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("")
    public ResponseEntity<List<PostDTO>> getPostsForUser(@RequestParam("postId") Long userId){
        return new ResponseEntity<>(service.getPostsForUser(userId), HttpStatus.OK);
    }

    @GetMapping("random")
    public ResponseEntity<List<PostDTO>> getRandomPosts(@RequestParam("pageNo") Integer pageNo,
                                                        @RequestParam("pageSize") Integer pageSize) {
        return ResponseEntity.ok( service.getRandomPosts(pageNo,pageSize) );
    }

    @DeleteMapping("delete")
    public ResponseEntity<Void> deletePost(@RequestParam("postId") Long postId){
        try{
            service.deletePost(postId);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    //POST END-POINTS ENDED
    //COMMENT END-POINTS STARTS HERE

    @PostMapping("add-comment")
    public ResponseEntity<CommentDTO> createComment(@RequestBody CommentRequestDTO commentRequest){
        return new ResponseEntity<>(service.createComment(commentRequest), HttpStatus.CREATED);
    }

    @DeleteMapping("remove-comment")
    public ResponseEntity<Void> deleteComment(@RequestParam("commentId") Long commentId){
        try{
            service.deleteComment(commentId);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("all-comments")
    public ResponseEntity<List<CommentDTO>> getCommentsForPost(@RequestParam("postId") Long postId){
        return new ResponseEntity<>(service.getCommentsForPost(postId), HttpStatus.OK);
    }

    @GetMapping("single-comment")
    public ResponseEntity<CommentDTO> getComment(@RequestParam("commentId") Long commentId){
        return new ResponseEntity<>(service.getComment(commentId), HttpStatus.OK);
    }

    @GetMapping("total-comment")
    public ResponseEntity<Long> getCommentCount(@RequestParam("postId") Long postId){
        return ResponseEntity.ok(service.commentCount(postId));
    }

    //COMMENT END-POINTS ENDED
    //LIKE END-POINTS STARTS HERE

    @PostMapping("add-like")
    public ResponseEntity<LikeDTO> createLike(@RequestBody LikeRequestDTO likeDTO){
        return new ResponseEntity<>(service.createLike(likeDTO), HttpStatus.OK);
    }

    @GetMapping("single-like")
    public ResponseEntity<LikeDTO> getLike(@RequestParam("likeId") Long likeId){
        return ResponseEntity.ok().body(service.getLike(likeId));
    }

    @GetMapping("all-like")
    public ResponseEntity<List<LikeDTO>> getLikesForPost(@RequestParam("postId") Long postId){
        return new ResponseEntity<>(service.getLikesForPost(postId), HttpStatus.OK);
    }

    @DeleteMapping("remove-like")
    public ResponseEntity<Void> deleteLike(@RequestParam("likeId") Long likeId){
        try{
            service.deleteLike(likeId);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("total-like")
    public ResponseEntity<Long> getLikeCount(@RequestParam("postId")  Long postId){
        return ResponseEntity.ok(service.likeCount(postId));
    }

    @GetMapping("post-count")
    public ResponseEntity<Long> getTotalPosts(){
        return ResponseEntity.ok(service.postCount());
    }

    @PutMapping("block-post")
    public ResponseEntity<Void> blockPost(@RequestParam("postId") Long postId){
        PostDTO postDTO = service.blockPost(postId);
        if(postDTO == null)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok().build();
    }

    @PutMapping("unblock-post")
    public ResponseEntity<Void> unBlockPost(@RequestParam("postId") Long postId){
        PostDTO postDTO = service.unBlockPost(postId);
        if(postDTO == null)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok().build();
    }

    @PutMapping("block-comment")
    public ResponseEntity<Void> blockComment(@RequestParam("commentId") Long commentId){
        CommentDTO commentDTO = service.blockComment(commentId);
        if(commentDTO == null)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok().build();
    }

    @PutMapping("unblock-comment")
    public ResponseEntity<Void> unBlockComment(@RequestParam("commentId") Long commentId){
        CommentDTO commentDTO = service.unBlockComment(commentId);
        if(commentDTO == null)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.ok().build();
    }
}