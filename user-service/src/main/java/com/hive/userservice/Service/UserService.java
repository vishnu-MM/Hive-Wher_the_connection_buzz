package com.hive.userservice.Service;

import com.hive.userservice.DTO.ImageDTO;
import com.hive.userservice.DTO.PaginationInfo;
import com.hive.userservice.DTO.UserDTO;
import com.hive.userservice.DTO.UserFilterDTO;
import com.hive.userservice.Exception.InvalidUserDetailsException;
import com.hive.userservice.Exception.UserNotFoundException;
import com.hive.userservice.Utility.ImageType;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface UserService {
    //* User
    UserDTO findUserByUsername(String username) throws UserNotFoundException;
    UserDTO findUserByEmail(String Email) throws UserNotFoundException;
    UserDTO findUserById(Long id) throws UserNotFoundException;
    UserDTO profileUpdate(UserDTO user, String authHeader) throws UserNotFoundException, InvalidUserDetailsException;
    UserDTO getCurrentUserProfile(String authorizationHeader) throws UserNotFoundException;
    Boolean existsUserById(Long id);
    void blockUser(Long id, String reason) throws UserNotFoundException;
    void unBlockUser(Long id) throws UserNotFoundException;
    Long getTotalUsers();
    PaginationInfo getAllUser(Integer pageNo, Integer pageSize);
    List<UserDTO> search(String searchQuery);
    PaginationInfo filter(UserFilterDTO userFilter);
    Map<String, Integer> getUserCountByMonth(LocalDate startDate, LocalDate endDate);
    Map<String, Integer> getUserCountByWeek(LocalDate startDate, LocalDate endDate);
    Map<String, Integer> getUserCountByYear(LocalDate startDate, LocalDate endDate);
    //* Image
    ImageDTO saveImage(MultipartFile file, ImageType imageType, String authHeader) throws UserNotFoundException, IOException;
    ImageDTO getImageByUserAndImageType(Long userId, ImageType imageType) throws UserNotFoundException;
    ImageDTO getImageByImageId(Long imageId);
    Boolean existsImageByUserAndImageType(Long userId, ImageType imageType) throws UserNotFoundException;
    Boolean existsImageByImageId(Long imageId);



}