package com.hive.userservice.Repository;

import com.hive.userservice.Entity.Complaints;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.sql.Date;
import java.util.List;

@Repository
public interface ComplaintsDAO extends JpaRepository<Complaints, Long> {
    // Filter
    Page<Complaints> findByDate(  Date date, Pageable pageable);
    Page<Complaints> findByDateBetween(  Date startDate, Date endDate, Pageable pageable);
    Page<Complaints> findByReportedUserIsBlocked(Boolean isBlocked, Pageable pageable);
    Page<Complaints> findByReportedUserIsBlockedAndDate(Boolean isBlocked,  Date date, Pageable pageable);
    Page<Complaints> findByReportedUserIsBlockedAndDateBetween(Boolean isBlocked, Date startDate, Date endDate, Pageable pageable);
    // Search
    List<Complaints> findByReportedUserUsernameContainingIgnoreCase(String search);
    List<Complaints> findByReportedUserNameContainingIgnoreCase(String search);
    List<Complaints> findByReportedUserEmailContainingIgnoreCase(String search);
    List<Complaints> findByDescriptionContainingIgnoreCase(String search);
}
