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
    private Long id; //1

    @Column(name = "username", unique = true, nullable = false)
    private String username; //2

    @Column(name = "email", unique = true, nullable = false )
    private String email; //3

    @Column(name = "password", nullable = false)
    private String password; //4

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role; //5

    @Column(name = "joined_date", nullable = false, updatable = false)
    private Date joinDate; //6

    @Column(name = "name", nullable = true)
    private String name; //7

    @Column(name = "phone", unique = true, nullable = true)
    private String phone; //8

    @Column(name = "about_me", nullable = true, length = 500)
    private String aboutMe; //9

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified; //10

    @Column(name = "is_blocked", nullable = false)
    private Boolean isBlocked; //11
}