package com.hive.authserver.DTO;

import com.hive.authserver.Utility.Role;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private Long id; //1
    private String username; //2
    private String email; //3
    private String password; //4
    private Role role; //5
    private Date joinDate; //6
    private String name; //7
    private String phone; //8
    private String aboutMe; //9
    private Boolean isVerified; //10
    private Boolean isBlocked; //11
    private String blockReason; //12
}