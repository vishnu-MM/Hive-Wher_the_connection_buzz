package com.hive.userservice.Service;

import com.hive.DTO.Notification;
import com.hive.Utility.NotificationType;
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
    private final MessageQueueService mqService;

    @Override
    public List<User> getConnectionForUser(Long userId, boolean isAscendingOrder) {
        if (isAscendingOrder) {
            return dao.findFriendsByUserIdAsc(userId, ConnectionStatue.FRIENDS);
        }
        return dao.findFriendsByUserIdDesc(userId, ConnectionStatue.FRIENDS);
    }

    @Override
    public List<Long> getConnectionForUserIds(Long userId) {
        Sort sort = Sort.by("date").descending();
        List<UserConnection> userConnectionList =  dao.findByUserIdAndStatus(userId, ConnectionStatue.FRIENDS, sort);
        return userConnectionList.stream().map(user -> user.getFriend().getId()).toList();
    }

    @Override
    public Long getConnectionCount(Long userId) {
        return dao.countByUserIdAndStatus(userId, ConnectionStatue.FRIENDS);
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
        Optional<UserConnection> connection1Opt = dao.findByUserAndFriend(connection.getUser(), connection.getFriend());
        Optional<UserConnection> connection2Opt = dao.findByUserAndFriend(connection.getFriend(), connection.getUser());
        if (connection1Opt.isPresent() && connection2Opt.isPresent()) {
            return entityToDTO(connection1Opt.get());
        }

        connection = dao.save(connection);
        UserConnection connection2 = UserConnection.builder()
                .user(connection.getFriend())
                .friend( connection.getUser())
                .status(connection.getStatus())
                .date(connection.getDate())
                .build();
        dao.save(connection2);

        // Notification: New Friend Request
        NotificationType notificationType = NotificationType.FRIEND_REQUEST;
        sentNotification(notificationType, connection);

        return entityToDTO(connection);
    }

    @Transactional
    private void deleteConnection(UserConnection connection) {
        Optional<UserConnection> connection1Opt = dao.findByUserAndFriend(connection.getUser(), connection.getFriend());
        Optional<UserConnection> connection2Opt = dao.findByUserAndFriend(connection.getFriend(), connection.getUser());
        connection1Opt.ifPresent(dao::delete);
        connection2Opt.ifPresent(dao::delete);
    }

    @Transactional
    private ConnectionDTO updateConnection(UserConnection connection) {
        Optional<UserConnection> connection1Opt = dao.findByUserAndFriend(connection.getUser(), connection.getFriend());
        Optional<UserConnection> connection2Opt = dao.findByUserAndFriend(connection.getFriend(), connection.getUser());
        if (connection1Opt.isEmpty() || connection2Opt.isEmpty()) {
            connection.setStatus(ConnectionStatue.REJECTED);
            return entityToDTO(connection);
        }
        UserConnection connection2 = connection1Opt.get();
        connection2.setStatus(ConnectionStatue.FRIENDS);
        dao.save(connection2);
        connection = connection2Opt.get();
        connection.setStatus(ConnectionStatue.FRIENDS);
        connection = dao.save(connection);

        // Notification: Friend Got Request Accepted
        NotificationType notificationType = NotificationType.FRIEND_REQUEST_ACCEPTED;
        sentNotification(notificationType, connection);

        return entityToDTO(connection);
    }

    private void sentNotification(NotificationType notificationType, UserConnection connection) {
        Long senderId = connection.getUser().getId();
        Long recipientId = connection.getFriend().getId();
        String topic = "notification";

        Notification notification = new Notification();
        notification.setSenderId(senderId);
        notification.setRecipientId(recipientId);
        notification.setNotificationType(notificationType);

        mqService.sendMessageToTopic(topic, notification);
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
