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
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Page<User> findUsersByRole(Role role, Pageable pageable);

    List<User> findUsersByUsernameContainingIgnoreCaseAndRole(String search, Role role);
    List<User> findUsersByNameContainingIgnoreCaseAndRole(String search, Role role);
    List<User> findUsersByEmailContainingIgnoreCaseAndRole(String search, Role role);

    Integer countAllByJoinDate(Date joinDate);
    @Query("SELECT COUNT(u) FROM User u where year(u.joinDate) = ?1 and month(u.joinDate) = ?2")
    Integer countAllByDateYearAndDateMonth(int year, int month);

    //When isBlocked is All and joinDate
    Page<User> findByRoleAndJoinDate(Role role, Date joinDate, Pageable pageable);
    Page<User> findByRoleAndJoinDateBetween(Role role, Date startDate, Date endDate, Pageable pageable);

    //When joinDate is All and isBlocked
    Page<User> findByRoleAndIsBlocked(Role role, Boolean isBlocked, Pageable pageable);

    //When joinDate and isBlocked
    Page<User> findByRoleAndIsBlockedAndJoinDate(Role role, Boolean isBlocked, Date joinDate, Pageable pageable);
    Page<User> findByRoleAndIsBlockedAndJoinDateBetween(Role role, Boolean isBlocked, Date startDate, Date endDate, Pageable pageable);

}