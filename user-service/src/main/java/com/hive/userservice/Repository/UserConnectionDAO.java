package com.hive.userservice.Repository;

import com.hive.userservice.Entity.User;
import com.hive.userservice.Entity.UserConnection;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserConnectionDAO extends JpaRepository<UserConnection, Long> {
    List<UserConnection> findByUserId(Long userId, Sort sort);
    Optional<UserConnection> findByUserAndFriend(User user, User friend);
}
