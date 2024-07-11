package com.hive.userservice.Repository;

import com.hive.userservice.Entity.User;
import com.hive.userservice.Entity.UserConnection;
import com.hive.userservice.Utility.ConnectionStatue;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserConnectionDAO extends JpaRepository<UserConnection, Long> {
    List<UserConnection> findByUserIdAndStatus(Long userId, ConnectionStatue status, Sort sort);
    Optional<UserConnection> findByUserAndFriend(User user, User friend);
    Long countByUserIdAndStatus(Long userId, ConnectionStatue status);

    @Query("SELECT uc.friend FROM UserConnection uc WHERE uc.user.id = :userId and uc.status = :status ORDER BY uc.date " +
            "DESC")
    List<User> findFriendsByUserIdDesc(@Param("userId") Long userId, @Param("status") ConnectionStatue status);

    @Query("SELECT uc.friend FROM UserConnection uc WHERE uc.user.id = :userId and uc.status = :status ORDER BY uc.date ASC")
    List<User> findFriendsByUserIdAsc(@Param("userId") Long userId, @Param("status") ConnectionStatue status);

    void deleteByUserId(Long userId);
    void deleteByFriendId(Long userId);
}
