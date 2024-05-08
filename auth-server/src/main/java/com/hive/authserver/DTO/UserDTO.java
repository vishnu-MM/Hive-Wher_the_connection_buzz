package com.hive.authserver.DTO;

import com.hive.authserver.Utility.Role;
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
    private Long id;
    private String username;
    private String email;
    private String password;
    private Role role;
    private Date joinDate;
    private String name;
    private String phone;
    private String aboutMe;
}