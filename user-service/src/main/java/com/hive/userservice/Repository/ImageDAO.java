package com.hive.userservice.Repository;

import com.hive.userservice.Entity.Image;
import com.hive.userservice.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageDAO extends JpaRepository<Image, Long> {
    Optional<Image> findByUser(User user);
    Boolean existsByUser(User user);
}