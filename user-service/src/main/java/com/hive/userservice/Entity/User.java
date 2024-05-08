package com.hive.userservice.Entity;

import com.hive.userservice.Utility.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Date;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "email", unique = true, nullable = false )
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "joined_date", nullable = false, updatable = false)
    private Date joinDate;

    @Column(name = "name", nullable = true)
    private String name;

    @Column(name = "phone", unique = true, nullable = true)
    private String phone;

    @Column(name = "about_me", nullable = true, length = 500)
    private String aboutMe;
}