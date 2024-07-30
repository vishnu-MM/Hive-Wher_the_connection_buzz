package com.hive.userservice.Service;

import com.hive.userservice.Entity.User;
import java.time.LocalDate;
import java.util.Map;

public interface DeletedUserService {
    void save(User user);
    Map<String, Integer> getGraphData(String filterBy);
    Map<String, Integer> getUserCountByMonth(LocalDate startDate, LocalDate endDate);
    Map<String, Integer> getUserCountByWeek(LocalDate startDate, LocalDate endDate);
    Map<String, Integer> getUserCountByYear(LocalDate startDate, LocalDate endDate);
    Long getDeletedUserCount();
}
