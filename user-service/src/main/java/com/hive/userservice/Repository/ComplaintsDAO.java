package com.hive.userservice.Repository;

import com.hive.userservice.Entity.Complaints;
import com.hive.userservice.Entity.User;
import com.hive.userservice.Utility.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComplaintsDAO extends JpaRepository<Complaints, Long> {
}