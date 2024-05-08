package com.hive.userservice.Service;

import com.hive.userservice.DTO.UserRequestDTO;
import com.hive.userservice.DTO.UserResponseDTO;
import com.hive.userservice.DTO.UserSignUpDTO;
import com.hive.userservice.Entity.User;
import com.hive.userservice.Repository.UserDAO;
import com.hive.userservice.Utility.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDAO dao;

    @Override
    public Boolean existsByEmail(String email) {
        return dao.existsByEmail(email);
    }

    @Override
    public Boolean existsByUsername(String username) {
        return dao.existsByUsername(username);
    }

    @Override
    public UserResponseDTO userRegister(UserSignUpDTO newUser) {
        Date date = new Date( new java.util.Date().getTime() );
        User user = User.builder()
                .email(newUser.getEmail())
                .username(newUser.getUsername())
                .password(newUser.getPassword())
                .role(Role.USER)
                .joinDate(date)
                .build();
        return entityToDTO(dao.save(user));
    }

    @Override
    public UserResponseDTO profileUpdate(UserRequestDTO user) {
        return null;
    }

    private UserResponseDTO entityToDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .role(user.getRole())
                .joinDate(user.getJoinDate())
                .name(user.getName())
                .phone(user.getPhone())
                .aboutMe(user.getAboutMe())
                .build();
    }
}