package com.hive.userservice.Service;

import com.hive.userservice.DTO.*;
import com.hive.userservice.Entity.Complaints;
import com.hive.userservice.Entity.User;
import com.hive.userservice.Exception.UserNotFoundException;
import com.hive.userservice.Repository.ComplaintsDAO;
import com.hive.userservice.Repository.UserDAO;
import com.hive.userservice.Utility.BlockType;
import com.hive.userservice.Utility.DateFilter;
import com.hive.userservice.Utility.Role;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ComplaintsServiceImpl implements ComplaintsService {
    private static final Logger log = LoggerFactory.getLogger(ComplaintsServiceImpl.class);
    private final ComplaintsDAO complaintsDAO;
    private final UserDAO userDAO;

    @Override
    @Transactional
    public void save(ComplaintsDTO complaintsDTO) {
        try {
            Complaints complaints = dtoToEntity(complaintsDTO);
            log.debug("Saving complaints: {}", complaints);
            complaintsDAO.save(complaints);
        } catch (UserNotFoundException e) {
            log.error("User not found when saving complaint: {}", complaintsDTO, e);
            throw new RuntimeException("Failed to save complaint due to missing user", e);
        } catch (Exception e) {
            log.error("An error occurred while saving the complaint: {}", complaintsDTO, e);
            throw new RuntimeException("Failed to save complaint", e);
        }
    }

    @Override
    public ComplaintsPage findAll(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("id").descending());
        Page<Complaints> page = complaintsDAO.findAll(pageable);
        
        List<ComplaintsDTO> contents = page.getContent().stream().map(this::entityToDTO).toList();
        return ComplaintsPage.builder()
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
    public ComplaintsPage filter(FilterDTO filterDTO) {
        Integer pageNo = filterDTO.getPageNo();
        Integer pageSize = filterDTO.getPageSize();
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("date").ascending());

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
            return findAll(pageNo, pageSize);
        }
    }

    @Override
    public List<ComplaintsDTO> search(String searchQuery) {
        Set<Complaints> complaintsList = new HashSet<>();
        complaintsList.addAll(complaintsDAO.findByDescriptionContainingIgnoreCase(searchQuery));
        complaintsList.addAll(complaintsDAO.findByReportedUserUsernameContainingIgnoreCase(searchQuery));
        complaintsList.addAll(complaintsDAO.findByReportedUserNameContainingIgnoreCase(searchQuery));
        complaintsList.addAll(complaintsDAO.findByReportedUserEmailContainingIgnoreCase(searchQuery));
        return complaintsList.stream().map(this::entityToDTO).toList();
    }

    private ComplaintsPage filterByBlockOnly(FilterDTO filterDTO, Pageable pageable) {
        boolean isBlocked = (BlockType.BLOCKED == filterDTO.getBlock());
        Page<Complaints> page = complaintsDAO.findByReportedUserIsBlocked(isBlocked, pageable);
        return pageToPaginationInfo(page);
    }

    private ComplaintsPage filterByDateOnly(FilterDTO filterDTO, Pageable pageable) {
        Date startDate = new Date(filterDTO.getStartingDate().getTime());
        Date endDate = new Date(filterDTO.getEndingDate().getTime());

        if (filterDTO.getTime() == DateFilter.TODAY || startDate.equals(endDate)) {
            Page<Complaints> page = complaintsDAO.findByDate(startDate, pageable);
            return pageToPaginationInfo(page);
        } else {
            Page<Complaints> page = complaintsDAO.findByDateBetween(startDate, endDate, pageable);
            return pageToPaginationInfo(page);
        }
    }

    private ComplaintsPage filterByBlockAndDate(FilterDTO filterDTO, Pageable pageable) {
        Date startDate = new Date(filterDTO.getStartingDate().getTime());
        Date endDate = new Date(filterDTO.getEndingDate().getTime());
        boolean isBlocked = (BlockType.BLOCKED == filterDTO.getBlock());

        if (filterDTO.getTime() == DateFilter.TODAY || startDate.equals(endDate)) {
            Page<Complaints> page = complaintsDAO.findByReportedUserIsBlockedAndDate(isBlocked, startDate, pageable);
            return pageToPaginationInfo(page);
        } else {
            Page<Complaints> page = complaintsDAO.findByReportedUserIsBlockedAndDateBetween(isBlocked,startDate,endDate,pageable);
            return pageToPaginationInfo(page);
        }
    }

    private User findUserById(Long userId) throws UserNotFoundException {
        return userDAO
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException("[ComplaintsServiceImpl] User with userID: "+userId));

    }

    private ComplaintsDTO entityToDTO(Complaints complaints) {
        return ComplaintsDTO.builder()
                .id(complaints.getId()) //1
                .senderId(complaints.getSenderId().getId()) //2
                .reportedUser(complaints.getReportedUser().getId()) //3
                .date(complaints.getDate()) //4
                .description(complaints.getDescription()) //5
                .build();
    }
    private Complaints dtoToEntity(ComplaintsDTO complaintsDTO) throws UserNotFoundException {
        User sender = findUserById(complaintsDTO.getSenderId());
        User reportedUser = findUserById(complaintsDTO.getReportedUser());
        return Complaints.builder()
                .id(complaintsDTO.getId()) //1
                .senderId(sender) //2
                .reportedUser(reportedUser) //3
                .date(complaintsDTO.getDate()) //4
                .description(complaintsDTO.getDescription()) //5
                .build();
    }

    private ComplaintsPage pageToPaginationInfo(Page<Complaints> page) {
        List<ComplaintsDTO> contents = page.getContent().stream().map(this::entityToDTO).toList();
        return ComplaintsPage.builder()
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
