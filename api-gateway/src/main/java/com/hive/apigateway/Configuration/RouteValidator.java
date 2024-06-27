package com.hive.apigateway.Configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import java.util.List;
import java.util.function.Predicate;

@Configuration
public class RouteValidator {

    public static final List<String> openApiEndpoints = List.of(
        "/eureka",
        "/api/auth/register",
        "/api/auth/send-otp",
        "/api/auth/verify-otp",
        "/api/auth/login",
        "/api/auth/check-email",
        "/api/auth/check-username",
        "/api/auth/validate",
        "/api/auth/get-username",
        "/api/auth/google-auth-url",
        "/api/auth/google-auth-register"
    );

    public Predicate<ServerHttpRequest> isSecured = request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));
}