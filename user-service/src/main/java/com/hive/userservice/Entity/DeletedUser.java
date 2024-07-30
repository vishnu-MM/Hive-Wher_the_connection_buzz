package com.hive.userservice.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Date;

@Entity
@Table(name = "deleted_account")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeletedUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", updatable = false)
    private String name;

    @Column(name = "username", nullable = false, updatable = false)
    private String username;

    @Column(name = "email", nullable = false, updatable = false )
    private String email;

    @Column(name = "joined_date", nullable = false, updatable = false)
    private Date joinDate;

    @Column(name = "deleted_date", nullable = false, updatable = false)
    private Date deletedDate;
}