package com.hive.userservice.Service;

import com.hive.userservice.DTO.ConnectionDTO;
import com.hive.userservice.Entity.User;
import com.hive.userservice.Entity.UserConnection;
import com.hive.userservice.Exception.UserNotFoundException;
import com.hive.userservice.Repository.UserConnectionDAO;
import com.hive.userservice.Repository.UserDAO;
import com.hive.userservice.Utility.ConnectionStatue;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserConnectionServiceImpl implements UserConnectionService{
    private final UserDAO userDao;
    private final UserConnectionDAO dao;

    @Override
    public List<User> getConnectionForUser(Long userId) {
        Sort sort =Sort.by("date").descending();
        List<UserConnection> userConnectionList =  dao.findByUserId(userId, sort);
        return userConnectionList.stream().map(UserConnection::getFriend).toList();
    }

    @Override
    public ConnectionDTO currentRelation(Long senderId, Long recipientId) throws UserNotFoundException {
        User user = getUser(senderId);
        User friend = getUser(recipientId);
        Optional<UserConnection> connectionOpt = dao.findByUserAndFriend(user, friend);
        if (connectionOpt.isPresent()) {
            return entityToDTO(connectionOpt.get());
        }
        return ConnectionDTO.builder()
                .senderId(senderId)
                .recipientId(recipientId)
                .status(ConnectionStatue.NOT_FRIENDS)
                .date(new Date(new java.util.Date().getTime()))
                .build();
    }


    @Override
    public ConnectionDTO processFriendRequest(ConnectionDTO connectionDTO) throws UserNotFoundException {
        ConnectionStatue status = connectionDTO.getStatus();
        UserConnection connection = dtoTOEntity(connectionDTO);

        if (status == ConnectionStatue.REQUESTED) {
            connectionDTO = saveNewConnection(connection);
        } else if (status == ConnectionStatue.REJECTED) {
            deleteConnection(connection);
        } else if (status == ConnectionStatue.ACCEPTED) {
            connectionDTO = updateConnection(connection);
        }
        return connectionDTO;
    }

    @Transactional
    private ConnectionDTO saveNewConnection(UserConnection connection) {
        Optional<UserConnection> connectionOpt = dao.findByUserAndFriend(connection.getUser(), connection.getFriend());
        if (connectionOpt.isPresent()) {
            return entityToDTO(connectionOpt.get());
        }
        connection = dao.save(connection);
        // Notification: New Friend Request
        return entityToDTO(connection);
    }

    @Transactional
    private void deleteConnection(UserConnection connection) {
        Optional<UserConnection> connectionOpt = dao.findByUserAndFriend(connection.getUser(), connection.getFriend());
        connectionOpt.ifPresent(dao::delete);
        // Notification: Friend Got Request Rejected
    }

    @Transactional
    private ConnectionDTO updateConnection(UserConnection connection) {
        Optional<UserConnection> connectionOpt = dao.findByUserAndFriend(connection.getUser(), connection.getFriend());
        if (connectionOpt.isEmpty()) {
            connection.setStatus(ConnectionStatue.REJECTED);
            return entityToDTO(connection);
        }
        connection = connectionOpt.get();
        connection.setStatus(ConnectionStatue.FRIENDS);
        connection = dao.save(connection);
        // Notification: Friend Got Request Accepted
        return entityToDTO(connection);
    }

    private User getUser(Long id) throws UserNotFoundException {
        return userDao.findById(id).orElseThrow(() -> new UserNotFoundException("[getUser] User with userID: "+id));
    }

    private UserConnection dtoTOEntity(ConnectionDTO dto) throws UserNotFoundException{
        User user = getUser(dto.getSenderId());
        User friend = getUser(dto.getRecipientId());
        return UserConnection.builder()
                .id(dto.getId())
                .user(user)
                .friend(friend)
                .status(dto.getStatus())
                .date(dto.getDate())
                .build();
    }

    private ConnectionDTO entityToDTO(UserConnection entity) {
        return ConnectionDTO.builder()
                .id(entity.getId())
                .senderId(entity.getUser().getId())
                .recipientId(entity.getFriend().getId())
                .status(entity.getStatus())
                .date(entity.getDate())
                .build();
    }
}
