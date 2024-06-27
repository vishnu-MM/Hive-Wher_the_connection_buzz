package com.hive.authserver.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.hive.authserver.CustomException.UserExistsException;
import com.hive.authserver.DTO.AuthResponse;
import com.hive.authserver.Entity.User;
import com.hive.authserver.Repository.UserDAO;
import com.hive.authserver.Utility.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import com.google.api.services.oauth2.model.Userinfo;

@Service
@RequiredArgsConstructor
public class OAuthService {
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final UserDAO dao;
    @Value("${spring.security.oauth2.resourceserver.opaque-token.clientId}")
    private String clientId;

    @Value("${spring.security.oauth2.resourceserver.opaque-token.clientSecret}")
    private String clientSecret;
    private final static String redirectUri = "http://localhost:4200/OAuth";
    private final static List<String> scopes = Arrays.asList("email", "profile", "openid");

    public String getGoogleAuthUrl() {
        return new GoogleAuthorizationCodeRequestUrl(clientId, redirectUri, scopes).build();
    }

    public AuthResponse registerWithGoogleOAuth(String googleAuthCode)
    throws URISyntaxException, IOException, UserExistsException
    {
        NetHttpTransport netHttpTransport = new NetHttpTransport();
        GsonFactory gsonFactory =  new GsonFactory();
        String token = new GoogleAuthorizationCodeTokenRequest(
                netHttpTransport, gsonFactory, clientId, clientSecret, googleAuthCode, redirectUri
        ).execute().getAccessToken();

        GoogleCredential credential = new GoogleCredential().setAccessToken(token);
        Oauth2 oauth2 = new Oauth2.Builder(netHttpTransport, gsonFactory, credential).setApplicationName("Hive").build();
        Userinfo userInfo = oauth2.userinfo().get().execute();

        String email = userInfo.getEmail();
        if (authService.existsByEmail(email)) {
            throw new UserExistsException(email + "-is in use, User Exists with this email Id");
        }
        return registerWithGoogleOAuth(userInfo);
    }

    private AuthResponse registerWithGoogleOAuth(Userinfo userinfo) {
        String email = userinfo.getEmail();
        String username = getUniqueUsername(email);
        String name = userinfo.getName();
        String password = passwordEncoder.encode("asAS12!@");
        Date date = new Date( new java.util.Date().getTime() );
        Boolean isVerified = userinfo.isVerifiedEmail(); // todo

        User user = User.builder()
                .email(email)
                .username(username)
                .name(name)
                .password(password)
                .role(Role.USER)
                .joinDate(date)
                .isVerified(true) // todo
                .isBlocked(false)
                .blockReason("NOT BLOCKED")
                .build();

        return authService.getAuthResponse( dao.save(user) );
    }

    private String getUniqueUsername(String email) {
        String usernameOpt1 = getUserNameFromEmail(email, true);
        String usernameOpt2 = getUserNameFromEmail(email, false);
        String usernameOpt3 = generateUniqueUsername(usernameOpt1);
        if (!authService.existsByUsername(usernameOpt1)) {
            return usernameOpt1;
        }
        else if (!authService.existsByUsername(usernameOpt2)) {
            return usernameOpt2;
        }
        else if (!authService.existsByUsername(usernameOpt3)) {
            return usernameOpt3;
        }
        else {
            return usernameOpt3;
        }
    }

    private static String getUserNameFromEmail(String email, Boolean removeNumberFromUsername) {
        String emailUserPart = email.split("@")[0];
        if (removeNumberFromUsername) {
            String emailUserPartOnlyAlpha = emailUserPart.replaceAll("[^a-zA-Z]", "");
            return emailUserPartOnlyAlpha.isEmpty() ? emailUserPart : emailUserPartOnlyAlpha;
        }
        return emailUserPart;
    }

    private static String generateUniqueUsername(String emailUserPart) {
        String formattedDateTime = getFormattedDateTime();
        return emailUserPart + formattedDateTime;
    }

    private static String getFormattedDateTime() {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        String day = String.format("%02d", localDateTime.getDayOfMonth());
        String month = String.format("%02d", localDateTime.getMonthValue());
        String year = String.valueOf(localDateTime.getYear()).substring(2);
        String hour = String.format("%02d", localDateTime.getHour());
        String minute = String.format("%02d", localDateTime.getMinute());

        return (day + month + year + hour + minute);
    }

}