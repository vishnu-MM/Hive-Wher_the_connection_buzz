package com.hive.userservice.Repository;

import com.hive.userservice.Entity.User;
import com.hive.userservice.Utility.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserDAO extends JpaRepository<User,Long> {
    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Page<User> findUsersByRole(Role role, Pageable pageable);

    List<User> findUsersByUsernameContainingAndRole(String search, Role role);
    List<User> findUsersByNameContainingAndRole(String search, Role role);

    Integer countAllByJoinDate(Date joinDate);
    @Query("SELECT COUNT(u) FROM User u where year(u.joinDate) = ?1 and month(u.joinDate) = ?2")
    Integer countAllByDateYearAndDateMonth(int year, int month);
}