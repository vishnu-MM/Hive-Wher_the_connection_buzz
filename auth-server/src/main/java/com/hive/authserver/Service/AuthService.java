package com.hive.authserver.Service;

import com.hive.authserver.DTO.AuthResponse;
import com.hive.authserver.DTO.UserDTO;
import com.hive.authserver.DTO.UserSignInDTO;
import com.hive.authserver.DTO.UserSignUpDTO;
import com.hive.authserver.Entity.User;
import com.hive.authserver.Repository.UserDAO;
import com.hive.authserver.Utility.OtpVerificationStatus;
import com.hive.authserver.Utility.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.sql.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserDAO dao;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;

    public AuthResponse userRegister(UserSignUpDTO newUser) {
        Date date = new Date( new java.util.Date().getTime() );
        User user = User.builder()
                .email(newUser.getEmail())
                .username(newUser.getUsername())
                .password(passwordEncoder.encode(newUser.getPassword()))
                .role(Role.USER)
                .joinDate(date)
                .isVerified(false)
                .isBlocked(false)
                .build();
        dao.save(user);
        return new AuthResponse( jwtService.generateToken(user) );
    }

    public AuthResponse authenticate(UserSignInDTO userDTO) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken( userDTO.getUsername(), userDTO.getPassword() )
        );

        Optional<User> userOptional = dao.findByEmail(userDTO.getUsername());

        if(userOptional.isEmpty())
            throw new UsernameNotFoundException(userDTO.getUsername());

        User user = userOptional.get();
        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }

    public Boolean existsByEmail(String email) {
        return dao.existsByEmail(email);
    }

    public Boolean existsByUsername(String username) {
        return dao.existsByUsername(username);
    }

    public boolean validateToken(String token) {
        String username = jwtService.extractUsername(token);
        Optional<User> userOptional = dao.findByUsername(username);

        if(userOptional.isEmpty())
            throw new UsernameNotFoundException(username);

        User user = userOptional.get();
        return jwtService.isTokenValid(token, user);
    }

    public String sendOTP(String username) {
        Optional<User> userOptional = dao.findByEmail(username);

        if (userOptional.isEmpty())
            throw new UsernameNotFoundException(username);

        otpService.sendOTP(entityToDTO(userOptional.get()));
        return "OTP sent";
    }

    public OtpVerificationStatus validateOTP(String otp, String email) {
        OtpVerificationStatus status = otpService.verifyOTP(email, otp);
        if (OtpVerificationStatus.SUCCESS.equals(status)) {
            Boolean isVerified = dao.findIsVerifiedByEmail(email);
            if (!isVerified) {
                dao.updateIsVerifiedByEmail(true,email);
            }
        }
        return status;
    }

    public String getUsername(String authorizationHeader) {
        String token = authorizationHeader.replace("Bearer ", "");
        return jwtService.extractUsername(token);
    }

    private UserDTO entityToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .role(user.getRole())
                .joinDate(user.getJoinDate())
                .name(user.getName())
                .phone(user.getPhone())
                .aboutMe(user.getAboutMe())
                .build();
    }
}