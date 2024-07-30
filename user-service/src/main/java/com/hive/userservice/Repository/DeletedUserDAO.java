package com.hive.userservice.Repository;

import com.hive.userservice.Entity.DeletedUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Date;

@Repository
public interface DeletedUserDAO extends JpaRepository<DeletedUser, Long> {
    Integer countAllByDeletedDate(Date deletedDate);
    @Query("SELECT COUNT(d) FROM DeletedUser d where year(d.deletedDate) = ?1 and month(d.deletedDate) = ?2")
    Integer countAllByDateYearAndDateMonth(int year, int month);
}