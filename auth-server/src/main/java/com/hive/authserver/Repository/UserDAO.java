package com.hive.authserver.Repository;

import com.hive.authserver.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDAO extends JpaRepository<User,Long> {
    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    @Query("SELECT u.isVerified FROM User u WHERE u.email = :email")
    Boolean findIsVerifiedByEmail(String email);
    @Modifying
    @Query("UPDATE User u SET u.isVerified = :isVerified WHERE u.email = :email")
    void updateIsVerifiedByEmail(@Param("isVerified") boolean isVerified, @Param("email") String email);
}