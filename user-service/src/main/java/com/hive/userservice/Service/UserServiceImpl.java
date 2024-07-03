package com.hive.userservice.Service;

import com.hive.userservice.DTO.*;
import com.hive.userservice.Entity.*;
import com.hive.userservice.Exception.*;
import com.hive.userservice.Repository.*;
import com.hive.userservice.Utility.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

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
        String url = "http://localhost:8181/api/auth/get-username?Authorization=" + authorizationHeader;
        String username = restTemplate.exchange(url, HttpMethod.GET, null, String.class).getBody();
        return findUserByUsername(username);
    }

    @Override
    public Boolean existsUserById(Long id) {
        return userDao.existsById(id);
    }

    @Override
    public void blockUser(Long id, String reason) throws UserNotFoundException {
        User user = userDao
                .findById(id)
                .orElseThrow(() -> new UserNotFoundException("[findUserById] User with userID: "+id));
        user.setIsBlocked(true);
        user.setBlockReason(reason);
        userDao.save(user);
    }

    @Override
    public void unBlockUser(Long id) throws UserNotFoundException {
        User user = userDao
                .findById(id)
                .orElseThrow(() -> new UserNotFoundException("[findUserById] User with userID: "+id));
        user.setIsBlocked(false);
        user.setBlockReason("NOT BLOCKED");
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
                .blockReason(user.getBlockReason())
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
                .blockReason(userDTO.getBlockReason())
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

    @Override
    public PaginationInfo getAllUser(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("id"));
        Page<User> page = userDao.findUsersByRole(Role.USER,pageable);
        List<UserDTO> contents = page.getContent().stream().map(this::entityToDTO).toList();
        return PaginationInfo.builder()
                .contents(contents)
                .pageNo(page.getNumber())
                .pageSize(page.getSize())
                .hasNext(page.hasNext())
                .isLast(page.isLast())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Override
    public List<UserDTO> search(String searchQuery) {
        if (searchQuery.isEmpty()) {
            return List.of();
        }
        Set<User> userList = new HashSet<>();
        userList.addAll(userDao.findUsersByUsernameContainingIgnoreCaseAndRole(searchQuery, Role.USER));
        userList.addAll(userDao.findUsersByNameContainingIgnoreCaseAndRole(searchQuery, Role.USER));
        userList.addAll(userDao.findUsersByEmailContainingIgnoreCaseAndRole(searchQuery, Role.USER));
        return userList.stream().map(this::entityToDTO).toList();
    }

    @Override
    public PaginationInfo filter(FilterDTO filterDTO) {
        Integer pageNo = filterDTO.getPageNo();
        Integer pageSize = filterDTO.getPageSize();
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("joinDate").ascending());

        if (filterDTO.getTime() == DateFilter.ALL && filterDTO.getBlock() != BlockType.ALL) {
            return filterByBlockOnly(filterDTO, pageable);
        }
        else if (filterDTO.getTime() != DateFilter.ALL && filterDTO.getBlock() == BlockType.ALL) {
            return filterByDateOnly(filterDTO, pageable);
        }
        else if (filterDTO.getTime() != DateFilter.ALL) {
            return filterByBlockAndDate(filterDTO, pageable);
        }
        else {
            return getAllUser(pageNo, pageSize);
        }
    }

    private PaginationInfo filterByBlockOnly(FilterDTO filterDTO, Pageable pageable) {
        boolean isBlocked = (BlockType.BLOCKED == filterDTO.getBlock());
        Page<User> page = userDao.findByRoleAndIsBlocked(Role.USER, isBlocked, pageable);
        return pageToPaginationInfo(page);
    }

    private PaginationInfo filterByDateOnly(FilterDTO filterDTO, Pageable pageable) {
        Date startDate = new Date(filterDTO.getStartingDate().getTime());
        Date endDate = new Date(filterDTO.getEndingDate().getTime());

        if (filterDTO.getTime() == DateFilter.TODAY || startDate.equals(endDate)) {
            Page<User> page = userDao.findByRoleAndJoinDate(Role.USER, startDate, pageable);
            return pageToPaginationInfo(page);
        } else {
            Page<User> page = userDao.findByRoleAndJoinDateBetween(Role.USER, startDate, endDate, pageable);
            return pageToPaginationInfo(page);
        }
    }

    private PaginationInfo filterByBlockAndDate(FilterDTO filterDTO, Pageable pageable) {
        Date startDate = new Date(filterDTO.getStartingDate().getTime());
        Date endDate = new Date(filterDTO.getEndingDate().getTime());
        boolean isBlocked = (BlockType.BLOCKED == filterDTO.getBlock());

        if (filterDTO.getTime() == DateFilter.TODAY || startDate.equals(endDate)) {
            Page<User> page = userDao.findByRoleAndIsBlockedAndJoinDate(Role.USER, isBlocked,startDate,pageable);
            return pageToPaginationInfo(page);
        } else {
            Page<User> page = userDao.findByRoleAndIsBlockedAndJoinDateBetween(Role.USER,isBlocked,startDate,endDate,pageable);
            return pageToPaginationInfo(page);
        }
    }

    @Override
    public Map<String, Integer> getUserCountByMonth(LocalDate startDate, LocalDate endDate) {
        Map<String, Integer> dateCountMap = new HashMap<>(31);
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            dateCountMap.put(
                    String.valueOf(current.getDayOfMonth()), userDao.countAllByJoinDate(Date.valueOf(current))
            );
            System.out.println(String.valueOf(current.getDayOfMonth())+" " +dateCountMap.get(String.valueOf(current)));
            current = current.plusDays(1);
        }
        return dateCountMap;
    }

    @Override
    public Map<String, Integer> getUserCountByWeek(LocalDate startDate, LocalDate endDate) {
        Map<String, Integer> dateCountMap = new HashMap<>(31);
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            dateCountMap.put(
                    String.valueOf(current.getDayOfWeek()), userDao.countAllByJoinDate(Date.valueOf(current))
            );
            System.out.println(String.valueOf(current.getDayOfWeek())+" "+ String.valueOf(current)+" " +dateCountMap.get(String.valueOf(current)));
            current = current.plusDays(1);
        }
        return dateCountMap;
    }

    @Override
    public Map<String, Integer> getUserCountByYear(LocalDate startDate, LocalDate endDate) {
        Map<String, Integer> dateCountMap = new HashMap<>(12);
        LocalDate current = startDate;
        int year = current.getYear();
        while (current.isBefore(endDate) || current.isEqual(endDate)) {
            dateCountMap.put(
                    String.valueOf(current.getMonth()), userDao.countAllByDateYearAndDateMonth(year,current.getMonthValue())
            );
            current = current.plusMonths(1);
        }
        return dateCountMap;
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

    private PaginationInfo pageToPaginationInfo(Page<User> page) {
        List<UserDTO> contents = page.getContent().stream().map(this::entityToDTO).toList();
        return PaginationInfo.builder()
                .contents(contents)
                .pageNo(page.getNumber())
                .pageSize(page.getSize())
                .hasNext(page.hasNext())
                .isLast(page.isLast())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}