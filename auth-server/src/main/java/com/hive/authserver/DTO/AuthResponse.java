package com.hive.authserver.DTO;

import com.hive.authserver.Utility.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Long userId;
    private Role role;
    private String message;
}