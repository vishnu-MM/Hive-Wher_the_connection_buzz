package com.hive.authserver.Controller;

import com.hive.authserver.CustomException.UserBlockedException;
import com.hive.authserver.DTO.AuthResponse;
import com.hive.authserver.DTO.UserSignInDTO;
import com.hive.authserver.DTO.UserSignUpDTO;
import com.hive.authserver.Service.AuthService;
import com.hive.authserver.Utility.OtpVerificationStatus;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService service;

    @PostMapping("register")
    public ResponseEntity<AuthResponse> userRegister(@RequestBody UserSignUpDTO newUser){
        System.out.println("Getting request for user registration " + newUser);
        if (service.existsByEmail(newUser.getEmail()) || service.existsByUsername(newUser.getUsername()))
            return ResponseEntity.badRequest().build();

        return new ResponseEntity<>( service.userRegister(newUser), HttpStatus.CREATED );
    }

    @PostMapping("send-otp")
    public ResponseEntity<Map<String, String>> sendOTP(@RequestParam("email") String email) {
        String message = service.sendOTP(email);
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    @GetMapping("verify-otp")
    public ResponseEntity<OtpVerificationStatus> validateOTP(@RequestParam("otp") String otp,
                                                             @RequestParam("email") String email){
        return ResponseEntity.ok(service.validateOTP(otp, email));
    }

    @PostMapping("login")
    public ResponseEntity<AuthResponse> login(@RequestBody UserSignInDTO user) {
        try {
            service.isUserBlocked(user);
            AuthResponse authResponse = service.authenticate(user);
            return new ResponseEntity<>(authResponse, HttpStatus.OK);
        } catch (BadCredentialsException e) {
            AuthResponse authResponse = new AuthResponse(null, null, null, "Invalid Password");
            return new ResponseEntity<>(authResponse, HttpStatus.UNAUTHORIZED);
        } catch (UsernameNotFoundException e) {
            AuthResponse authResponse = new AuthResponse(null, null, null, "Invalid Email id");
            return new ResponseEntity<>(authResponse, HttpStatus.NOT_FOUND);
        } catch (UserBlockedException e) {
            AuthResponse authResponse = new AuthResponse(null, null, null, "User is blocked. Reason : "+ e.getMessage());
            return new ResponseEntity<>(authResponse, HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            log.error(e.getMessage());
            AuthResponse authResponse = new AuthResponse(null, null, null, "Internal Server Error");
            return new ResponseEntity<>(authResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
