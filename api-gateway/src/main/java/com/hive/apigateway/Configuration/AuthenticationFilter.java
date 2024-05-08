package com.hive.apigateway.Configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.Objects;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired private RouteValidator routeValidator;
    @Autowired private WebClient.Builder webClientBuilder;

    public AuthenticationFilter() {
        super(Config.class);
    }

    public static class Config{}

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {

            if ( routeValidator.isSecured.test(exchange.getRequest())) {
                 HttpHeaders header = exchange.getRequest().getHeaders();

                if (! header.containsKey(HttpHeaders.AUTHORIZATION)) {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                }

                String authHeader = Objects.requireNonNull(header.get(HttpHeaders.AUTHORIZATION)).getFirst();
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                    System.out.println("auth header exists\n"+authHeader);
                }

                Mono<Boolean> responseMono = webClientBuilder.build()
                        .get()
                        .uri("http://localhost:8181/api/auth/validate?token=" + authHeader)
                        .retrieve()
                        .bodyToMono(Boolean.class);
                
                return responseMono.flatMap(isValid -> {
                    if (isValid != null && isValid) {
                        return chain.filter(exchange);
                    }
                    else {
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    }
                }).onErrorResume(error -> {
                    System.out.println("Error Occurred: " + error.getMessage());

                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                });

                /*
                responseMono.subscribe(
                        response -> {
                            // Handle the response here
                            System.out.println(response);
                            chain.filter(exchange);
                        },
                        error -> {
                            System.out.println("Error Occurred");
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            exchange.getResponse().setComplete();
                        });

                    *//*
                return responseMono.flatMap(isValid -> {
                    if (isValid != null && isValid) {
                        // Continue the filter chain
                        return chain.filter(exchange);
                    } else {
                        // Return unauthorized response
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    }
                });

 */
            }

            return chain.filter(exchange);
        });
    }
}