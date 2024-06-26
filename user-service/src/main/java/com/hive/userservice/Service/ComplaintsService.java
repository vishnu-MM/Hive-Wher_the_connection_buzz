package com.hive.userservice.Service;

import com.hive.userservice.DTO.ComplaintsDTO;
import com.hive.userservice.DTO.ComplaintsPage;
import com.hive.userservice.DTO.FilterDTO;

import java.util.List;

public interface ComplaintsService {
    void save(ComplaintsDTO complaintsDTO);
    ComplaintsPage findAll(Integer pageNo, Integer pageSize);
    ComplaintsPage filter(FilterDTO filterDTO);
    List<ComplaintsDTO> search(String searchQuery);
}
