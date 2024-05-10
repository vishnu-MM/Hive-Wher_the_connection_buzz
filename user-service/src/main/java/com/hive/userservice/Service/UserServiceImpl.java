package com.hive.userservice.Service;

import com.hive.userservice.DTO.ImageDTO;
import com.hive.userservice.DTO.UserDTO;
import com.hive.userservice.Entity.Image;
import com.hive.userservice.Entity.User;
import com.hive.userservice.Exception.InvalidUserDetailsException;
import com.hive.userservice.Exception.UserNotFoundException;
import com.hive.userservice.Repository.ImageDAO;
import com.hive.userservice.Repository.UserDAO;
import com.hive.userservice.Utility.ImageType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDAO userDao;
    private final ImageDAO imageDao;
    private final RestTemplate restTemplate;


    @Override
    public UserDTO findUserByUsername(String username) throws UserNotFoundException {
        Optional<User> userOptional = userDao.findByUsername(username);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("[findUserByUsername] User with Username: "+username);
        }
        return entityToDTO(userOptional.get());
    }

    @Override
    public UserDTO findUserByEmail(String email) throws UserNotFoundException {
        Optional<User> userOptional = userDao.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("[findUserByEmail] User with Email: "+email);
        }
        return entityToDTO(userOptional.get());
    }

    @Override
    public UserDTO findUserById(Long id) throws UserNotFoundException {
        Optional<User> userOptional = userDao.findById(id);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("[findUserById] User with userID: "+id);
        }
        return entityToDTO(userOptional.get());
    }

    @Override
    public UserDTO profileUpdate(UserDTO userDTO, String authHeader) throws UserNotFoundException, InvalidUserDetailsException {
        UserDTO currentUserDto = getCurrentUserProfile(authHeader);

        if ( !currentUserDto.getUsername().equals( userDTO.getUsername() ) )
            throw new InvalidUserDetailsException(userDTO.getUsername());

        return entityToDTO( userDao.save( dtoTOEntity(userDTO)));
    }

    @Override
    public UserDTO getCurrentUserProfile(String authorizationHeader) throws UserNotFoundException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authorizationHeader);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = "http://localhost:8181/api/auth/get-username";
        String username = restTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();

        return findUserByUsername(username);
    }

    @Override
    public ImageDTO getProfileImageByUser(Long userId, ImageType imageType) throws UserNotFoundException {
        Optional<User> userOptional = userDao.findById(userId);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("[getProfileImageByUser] User with userID: "+userId);
        }
        return null;
    }

    @Override
    public ImageDTO getProfileImage(Long imageId) {
        return null;
    }


    @Override
    public ImageDTO saveProfileImage(Long userId, ImageType imageType) {
        return null;
    }

    @Override
    public void deleteProfileImage(Long imageId) {

    }

    private UserDTO entityToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId()) //1
                .username(user.getUsername()) //2
                .email(user.getEmail()) //3
                .password(user.getPassword()) //4
                .role(user.getRole()) //5
                .joinDate(user.getJoinDate()) //6
                .name(user.getName()) //7
                .phone(user.getPhone()) //8
                .aboutMe(user.getAboutMe()) //9
                .isVerified(user.getIsVerified()) //10
                .isBlocked(user.getIsBlocked()) //11
                .build();
    }
    private User dtoTOEntity(UserDTO userDTO) {
        return User.builder()
                .id(userDTO.getId()) //1
                .username(userDTO.getUsername()) //2
                .email(userDTO.getEmail()) //3
                .password(userDTO.getPassword()) //4
                .role(userDTO.getRole()) //5
                .joinDate(userDTO.getJoinDate()) //6
                .name(userDTO.getName()) //7
                .phone(userDTO.getPhone()) //8
                .aboutMe(userDTO.getAboutMe()) //9
                .isVerified(userDTO.getIsVerified()) //10
                .isBlocked(userDTO.getIsBlocked()) //11
                .build();
    }

    private ImageDTO entityToDTO(Image image) {
        return ImageDTO.builder()
                .id(image.getId())
                .name(image.getName())
                .type(image.getType())
                .image(image.getImage())
                .imageType(image.getImageType())
                .userID(image.getUser().getId())
                .build();
    }

    private Image dtoTOEntity(ImageDTO imageDTO) throws UserNotFoundException {
        Optional<User> userOptional = userDao.findById(imageDTO.getId());

        if (userOptional.isEmpty()) 
            throw new UserNotFoundException("[Image dtoTOEntity(ImageDTO imageDTO)] User with userID: "+ imageDTO.getUserID());

        User user = userOptional.get();
        return Image.builder()
                .id(imageDTO.getId())
                .name(imageDTO.getName())
                .type(imageDTO.getType())
                .image(imageDTO.getImage())
                .imageType(imageDTO.getImageType())
                .user(user)
                .build();
    }
}