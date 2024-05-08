package com.hive.userservice.DTO;

import com.hive.userservice.Utility.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDTO {
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
