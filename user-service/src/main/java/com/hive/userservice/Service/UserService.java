package com.hive.userservice.Service;

import com.hive.userservice.DTO.UserRequestDTO;
import com.hive.userservice.DTO.UserResponseDTO;
import com.hive.userservice.DTO.UserSignUpDTO;

public interface UserService {
    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);
    UserResponseDTO userRegister(UserSignUpDTO newUser);
    UserResponseDTO profileUpdate(UserRequestDTO user);
}
