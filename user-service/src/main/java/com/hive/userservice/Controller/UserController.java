package com.hive.userservice.Controller;

import com.hive.userservice.DTO.ImageDTO;
import com.hive.userservice.DTO.UserDTO;
import com.hive.userservice.Entity.Image;
import com.hive.userservice.Exception.InvalidUserDetailsException;
import com.hive.userservice.Exception.UserNotFoundException;
import com.hive.userservice.Service.UserService;
import com.hive.userservice.Utility.ImageType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @GetMapping("profile")
    public ResponseEntity<UserDTO> getMyProfile(@RequestHeader(name = "Authorization") String authorizationHeader) {
        try {
            return ResponseEntity.ok(service.getCurrentUserProfile(authorizationHeader));
        }
        catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("profile/{id}")
    public ResponseEntity<UserDTO> getProfile(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.findUserById(id));
        }
        catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("update")
    public ResponseEntity<UserDTO> profileUpdate(@RequestBody UserDTO user, @RequestHeader(name = "Authorization") String authHeader) {
        try {
            return new ResponseEntity<>( service.profileUpdate(user,authHeader), HttpStatus.CREATED );
        }
        catch (UserNotFoundException | InvalidUserDetailsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("upload/image")
    public ResponseEntity<ImageDTO> uploadImage(@RequestParam("image") MultipartFile file,
                                              @RequestParam("type") ImageType imageType,
                                              @RequestHeader(name = "Authorization") String authHeader) {
        try {
            return ResponseEntity.ok(service.saveImage(file, imageType, authHeader));
        }
        catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("image")
    public ResponseEntity<ImageDTO> getProfileImage(@RequestParam("userID") Long userId,
                                                    @RequestParam("type") ImageType imageType) {
        try {
            return ResponseEntity.ok(service.getImageByUserAndImageType(userId, imageType));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


    //todo : View My Profile
    //todo : Edit My Profile
    //todo : View Others Profile
    //todo : Sent Friend-Request
    //todo : Accept Friend-Request
    //todo : Report A User
    //todo : Search
}
