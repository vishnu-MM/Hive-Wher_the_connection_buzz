package com.hive.userservice.Controller;

import com.hive.userservice.DTO.UserRequestDTO;
import com.hive.userservice.DTO.UserResponseDTO;
import com.hive.userservice.DTO.UserSignUpDTO;
import com.hive.userservice.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @PostMapping("register")
    public ResponseEntity<UserResponseDTO> userRegister(@RequestBody UserSignUpDTO newUser){
        if ( service.existsByEmail(newUser.getEmail()) ) {
            return ResponseEntity.badRequest().build();
        }
        if ( service.existsByUsername(newUser.getUsername()) ) {
            return ResponseEntity.badRequest().build();
        }
        return new ResponseEntity<>( service.userRegister(newUser), HttpStatus.CREATED );
    }

    @PutMapping("update")
    public ResponseEntity<UserResponseDTO> profileUpdate(@RequestBody UserRequestDTO user) {
        return new ResponseEntity<>( service.profileUpdate(user), HttpStatus.CREATED );
    }

    @GetMapping("check-email")
    public ResponseEntity<Boolean> isEmailExists(@RequestParam String email) {
        return new ResponseEntity<>(service.existsByEmail(email), HttpStatus.OK);
    }

    @GetMapping("check-username")
    public ResponseEntity<Boolean> isUsernameExists(@RequestParam String username) {
        return new ResponseEntity<>(service.existsByUsername(username), HttpStatus.OK);
    }

    //todo : View My Profile
    //todo : Edit My Profile
    //todo : View Others Profile
    //todo : Sent Friend-Request
    //todo : Accept Friend-Request
    //todo : Report A User
    //todo : Search
}
