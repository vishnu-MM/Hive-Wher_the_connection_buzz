package com.hive.userservice.Service;

import com.hive.userservice.DTO.ComplaintsDTO;
import com.hive.userservice.DTO.ComplaintsPage;

public interface ComplaintsService {
    void save(ComplaintsDTO complaintsDTO);
    ComplaintsPage findAll(Integer pageNo, Integer pageSize);
}
