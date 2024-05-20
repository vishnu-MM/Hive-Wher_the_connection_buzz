package com.hive.userservice.Service;

import com.hive.userservice.DTO.*;
import com.hive.userservice.Entity.*;
import com.hive.userservice.Exception.*;
import com.hive.userservice.Repository.*;
import com.hive.userservice.Utility.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDAO userDao;
    private final ImageDAO imageDao;
    private final RestTemplate restTemplate;

    @Override
    public UserDTO findUserByUsername(String username) throws UserNotFoundException {
        return userDao.findByUsername(username)
                .map(this::entityToDTO)
                .orElseThrow(() -> new UserNotFoundException("[findUserByUsername] User with Username: "+username));
    }

    @Override
    public UserDTO findUserByEmail(String email) throws UserNotFoundException {
        return userDao.findByEmail(email)
                .map(this::entityToDTO)
                .orElseThrow(() -> new UserNotFoundException("[findUserByEmail] User with Email: "+email));
    }

    @Override
    public UserDTO findUserById(Long id) throws UserNotFoundException {
        return userDao.findById(id)
                .map(this::entityToDTO)
                .orElseThrow(() -> new UserNotFoundException("[findUserById] User with userID: "+id));
    }

    @Override
    public UserDTO profileUpdate(UserDTO userDTO, String authHeader) throws UserNotFoundException, InvalidUserDetailsException {
        UserDTO currentUserDto = getCurrentUserProfile(authHeader);

        if ( !currentUserDto.getId().equals( userDTO.getId() ) )
            throw new InvalidUserDetailsException(userDTO.getId().toString());

        userDTO.setPassword( currentUserDto.getPassword() );
        userDTO.setIsVerified( currentUserDto.getIsVerified() );
        userDTO.setIsBlocked( currentUserDto.getIsBlocked() );

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
    public Boolean existsUserById(Long id) {
        return userDao.existsById(id);
    }

    @Override
    public void blockUser(Long id) throws UserNotFoundException {
        User user = userDao
                .findById(id)
                .orElseThrow(() -> new UserNotFoundException("[findUserById] User with userID: "+id));
        user.setIsBlocked(true);
        userDao.save(user);
    }

    @Override
    public void unBlockUser(Long id) throws UserNotFoundException {
        User user = userDao
                .findById(id)
                .orElseThrow(() -> new UserNotFoundException("[findUserById] User with userID: "+id));
        user.setIsBlocked(false);
        userDao.save(user);
    }

    @Override
    public Long getTotalUsers() {
        return userDao.count();
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

// USER SERVICE METHODS ENDS HERE
// IMAGE SERVICES METHODS STARTS HERE

    @Override
    @Transactional
    public ImageDTO saveImage(MultipartFile file, ImageType imageType, String authHeader)
    throws UserNotFoundException, IOException {

        User user = dtoTOEntity( getCurrentUserProfile(authHeader) );
        Image image = Image.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .image(ImageUtil.compressImage(file.getBytes()))
                .imageType(imageType)
                .user(user)
                .build();
        imageDao.findByUserAndImageType(user, imageType)
                .ifPresent(value -> image.setId( value.getId() ));
        return entityToDTO(imageDao.save(image));
    }

    @Override
    @Transactional
    public ImageDTO getImageByUserAndImageType(Long userId, ImageType imageType) throws UserNotFoundException {
        User user = userDao.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("[getImageByUserAndImageType] User with userID: " + userId));

        return imageDao.findByUserAndImageType(user, imageType)
                .map(this::entityToDTO)
                .orElse(null);
    }

    @Override
    @Transactional
    public ImageDTO getImageByImageId(Long imageId) {
        return imageDao.findById(imageId)
                .map(this::entityToDTO)
                .orElseThrow(RuntimeException::new);
    }

    @Override
    public Boolean existsImageByUserAndImageType(Long userId, ImageType imageType) throws UserNotFoundException {
        User user = userDao.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("[getImageByUserAndImageType] User with userID: " + userId));
        return imageDao.existsByUserAndImageType(user, imageType);
    }

    @Override
    public Boolean existsImageByImageId(Long imageId) {
        return imageDao.existsById(imageId);
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
        User user = userDao.findById(imageDTO.getUserID())
                .orElseThrow(() -> new UserNotFoundException("[Image dtoTOEntity(ImageDTO)] User with userID: " + imageDTO.getUserID()));

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