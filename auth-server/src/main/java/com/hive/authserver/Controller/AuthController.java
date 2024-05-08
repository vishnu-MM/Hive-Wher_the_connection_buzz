package com.hive.authserver.Controller;

import com.hive.authserver.DTO.AuthResponse;
import com.hive.authserver.DTO.UserSignInDTO;
import com.hive.authserver.DTO.UserSignUpDTO;
import com.hive.authserver.Service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;

    @PostMapping("register")
    public ResponseEntity<AuthResponse> userRegister(@RequestBody UserSignUpDTO newUser){
        if (service.existsByEmail(newUser.getEmail()) || service.existsByUsername(newUser.getUsername()))
            return ResponseEntity.badRequest().build();

        return new ResponseEntity<>( service.userRegister(newUser), HttpStatus.CREATED );
    }

    @PostMapping("login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserSignInDTO user){
        return new ResponseEntity<>( service.authenticate(user), HttpStatus.OK );
    }

    @GetMapping("check-email")
    public ResponseEntity<Boolean> isEmailExists(@RequestParam String email) {
        return new ResponseEntity<>(service.existsByEmail(email), HttpStatus.OK);
    }

    @GetMapping("check-username")
    public ResponseEntity<Boolean> isUsernameExists(@RequestParam String username) {
        return new ResponseEntity<>(service.existsByUsername(username), HttpStatus.OK);
    }

    @GetMapping("/validate")
    public String validateToken( Principal principal) {
//        service.validateToken(token);
        return principal.getName();
    }
}
