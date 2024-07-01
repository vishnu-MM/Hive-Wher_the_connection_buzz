package com.hive.userservice.Service;

import com.hive.userservice.DTO.ConnectionDTO;
import com.hive.userservice.Entity.User;
import com.hive.userservice.Exception.UserNotFoundException;

import java.util.List;

public interface UserConnectionService {
    ConnectionDTO processFriendRequest(ConnectionDTO connectionDTO) throws UserNotFoundException;
    ConnectionDTO currentRelation(Long senderId, Long recipientId) throws UserNotFoundException;
    List<User> getConnectionForUser(Long userId);
    List<Long> getConnectionForUserIds(Long userId);
}
