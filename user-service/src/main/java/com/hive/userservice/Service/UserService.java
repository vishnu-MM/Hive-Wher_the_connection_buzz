package com.hive.userservice.Service;

import com.hive.userservice.DTO.ImageDTO;
import com.hive.userservice.DTO.UserDTO;
import com.hive.userservice.Exception.InvalidUserDetailsException;
import com.hive.userservice.Exception.UserNotFoundException;
import com.hive.userservice.Utility.ImageType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {
    //* User
    UserDTO findUserByUsername(String username) throws UserNotFoundException;
    UserDTO findUserByEmail(String Email) throws UserNotFoundException;
    UserDTO findUserById(Long id) throws UserNotFoundException;
    UserDTO profileUpdate(UserDTO user, String authHeader) throws UserNotFoundException, InvalidUserDetailsException;
    UserDTO getCurrentUserProfile(String authorizationHeader) throws UserNotFoundException;
    Boolean existsUserById(Long id);
    //* Image
    ImageDTO saveImage(MultipartFile file, ImageType imageType, String authHeader) throws UserNotFoundException, IOException;
    ImageDTO getImageByUserAndImageType(Long userId, ImageType imageType) throws UserNotFoundException;
    ImageDTO getImageByImageId(Long imageId);
    Boolean existsImageByUserAndImageType(Long userId, ImageType imageType) throws UserNotFoundException;
    Boolean existsImageByImageId(Long imageId);
}