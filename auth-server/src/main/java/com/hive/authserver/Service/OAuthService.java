package com.hive.authserver.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.hive.authserver.Entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import com.google.api.services.oauth2.model.Userinfo;

@Service
@RequiredArgsConstructor
public class OAuthService {
    @Value("${spring.security.oauth2.resourceserver.opaque-token.clientId}")
    private String clientId;

    @Value("${spring.security.oauth2.resourceserver.opaque-token.clientSecret}")
    private String clientSecret;
    private final static String redirectUri = "http://localhost:4200/OAuth";
    private final static List<String> scopes = Arrays.asList("email", "profile", "openid");

    public String getGoogleAuthUrl() {
        return new GoogleAuthorizationCodeRequestUrl(clientId, redirectUri, scopes).build();
    }

    public void registerWithGoogleOAuth(String googleAuthCode) throws URISyntaxException, IOException {
        NetHttpTransport netHttpTransport = new NetHttpTransport();
        GsonFactory gsonFactory =  new GsonFactory();
        String token = new GoogleAuthorizationCodeTokenRequest(
                netHttpTransport, gsonFactory, clientId, clientSecret, googleAuthCode, redirectUri
        ).execute()
        .getAccessToken();
        System.out.println(token);

        // Use the access token to fetch user details
        GoogleCredential credential = new GoogleCredential().setAccessToken(token);
        Oauth2 oauth2 = new Oauth2.Builder(new NetHttpTransport(), new GsonFactory(), credential)
                .setApplicationName("Hive").build();

        Userinfo userInfo = oauth2.userinfo().get().execute();
        System.out.println(userInfo);
        System.out.println("User Email: " + userInfo.getEmail());
        System.out.println("User Name: " + userInfo.getName());

        // Save user info to the database
//        User user = new User();
//        user.setEmail(userInfo.getEmail());
//        user.setName(userInfo.getName());
//        user.setPictureUrl(userInfo.getPicture());
    }

}
