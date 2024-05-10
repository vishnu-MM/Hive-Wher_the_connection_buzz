package com.hive.userservice.Service;

import com.hive.userservice.DTO.ImageDTO;
import com.hive.userservice.DTO.UserDTO;
import com.hive.userservice.Exception.InvalidUserDetailsException;
import com.hive.userservice.Exception.UserNotFoundException;
import com.hive.userservice.Utility.ImageType;

public interface UserService {
    //* User
    UserDTO findUserByUsername(String username) throws UserNotFoundException;
    UserDTO findUserByEmail(String Email) throws UserNotFoundException;
    UserDTO findUserById(Long id) throws UserNotFoundException;
    UserDTO profileUpdate(UserDTO user, String authHeader) throws UserNotFoundException, InvalidUserDetailsException;
    UserDTO getCurrentUserProfile(String authorizationHeader) throws UserNotFoundException;
    //* Image
    ImageDTO getProfileImageByUser(Long userId, ImageType imageType) throws UserNotFoundException;
    ImageDTO getProfileImage(Long imageId);
    ImageDTO saveProfileImage(Long userId, ImageType imageType);
    void deleteProfileImage(Long imageId);
}