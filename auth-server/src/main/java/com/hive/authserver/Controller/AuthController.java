package com.hive.authserver.Controller;

import com.hive.authserver.DTO.AuthResponse;
import com.hive.authserver.DTO.UserSignInDTO;
import com.hive.authserver.DTO.UserSignUpDTO;
import com.hive.authserver.Service.AuthService;
import com.hive.authserver.Utility.OtpVerificationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;

    @PostMapping("register")
    public ResponseEntity<AuthResponse> userRegister(@RequestBody UserSignUpDTO newUser){
        System.out.println("Getting request for user registration " + newUser);
        if (service.existsByEmail(newUser.getEmail()) || service.existsByUsername(newUser.getUsername()))
            return ResponseEntity.badRequest().build();

        return new ResponseEntity<>( service.userRegister(newUser), HttpStatus.CREATED );
    }

    @PostMapping("send-otp")
    public ResponseEntity<String> sentOTP(@RequestParam String username){
        return ResponseEntity.ok(service.sendOTP(username));
    }

    @GetMapping("verify-otp")
    public ResponseEntity<OtpVerificationStatus> validateOTP(@RequestParam String otp, @RequestParam String username){
        return ResponseEntity.ok(service.validateOTP(otp, username));
    }

    @PostMapping("login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserSignInDTO user){
        return new ResponseEntity<>( service.authenticate(user), HttpStatus.OK );
    }

    @GetMapping("check-email")
    public ResponseEntity<Boolean> isEmailExists(@RequestParam String email) {
        System.out.println("Request is getting with email " + email);
        return new ResponseEntity<>(service.existsByEmail(email), HttpStatus.OK);
    }

    @GetMapping("check-username")
    public ResponseEntity<Boolean> isUsernameExists(@RequestParam String username) {
        System.out.println("Request is getting with username " + username);
        return new ResponseEntity<>(service.existsByUsername(username), HttpStatus.OK);
    }

    @GetMapping("validate")
    @ResponseStatus(HttpStatus.OK)
    public Boolean validateToken(@RequestParam String token) {
        Boolean result= service.validateToken(token);
        System.out.println("returning "+result);
        return result;
    }

    @GetMapping("get-username")
    public ResponseEntity<String> getUsername(@RequestHeader(name = "Authorization") String authorizationHeader){
        return ResponseEntity.ok(service.getUsername(authorizationHeader));
    }
}
