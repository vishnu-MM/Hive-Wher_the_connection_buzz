package com.hive.userservice.Service;

import com.hive.userservice.Entity.DeletedUser;
import com.hive.userservice.Entity.User;
import com.hive.userservice.Repository.DeletedUserDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.sql.Date;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DeletedUserServiceImpl implements DeletedUserService {
    private final DeletedUserDAO dao;

    @Override
    public void save(User user) {
        Date deletedDate = new Date( new java.util.Date().getTime() );
        DeletedUser deletedUser = DeletedUser.builder()
                .name(user.getName())
                .username(user.getUsername())
                .email(user.getEmail())
                .joinDate(user.getJoinDate())
                .deletedDate(deletedDate)
                .build();
        dao.save(deletedUser);
    }

    @Override
    public Map<String, Integer> getGraphData(String filterBy) {
        switch (filterBy) {
            case "MONTH" -> {
                return getUserCountByMonth(getStartDateOfMonth(), LocalDate.now());
            }
            case "WEEK" -> {
                return getUserCountByWeek(getStartDateOfWeek(), LocalDate.now());
            }
            case "YEAR" -> {
                return getUserCountByYear(getStartDateOfYear(), LocalDate.now());
            }
            case null, default -> throw new RuntimeException("Invalid Filter");
        }
    }

    private LocalDate getStartDateOfMonth() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(endDate.getDayOfMonth() - 1);
        if (startDate.isAfter(endDate)) {
            throw new DateTimeException("Invalid Start and Ending date");
        }
        return startDate;
    }

    private LocalDate getStartDateOfYear() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = LocalDate.parse(endDate.getYear() + "-01-01");
        if (startDate.isAfter(endDate)) {
            throw new DateTimeException("Invalid Start and Ending date");
        }
        return startDate;
    }

    private LocalDate getStartDateOfWeek() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(endDate.getDayOfWeek().getValue());
        if (startDate.isAfter(endDate)) {
            throw new DateTimeException("Invalid Start and Ending date");
        }
        return startDate;
    }

    @Override
    public Map<String, Integer> getUserCountByMonth(LocalDate startDate, LocalDate endDate) {
        Map<String, Integer> dateCountMap = new HashMap<>(31);
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            dateCountMap.put(
                    String.valueOf(current.getDayOfMonth()), dao.countAllByDeletedDate(Date.valueOf(current))
            );
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
                    String.valueOf(current.getDayOfWeek()), dao.countAllByDeletedDate(Date.valueOf(current))
            );
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
                    String.valueOf(current.getMonth()), dao.countAllByDateYearAndDateMonth(year,current.getMonthValue())
            );
            current = current.plusMonths(1);
        }
        return dateCountMap;
    }

    @Override
    public Long getDeletedUserCount() {
        return dao.count();
    }
}