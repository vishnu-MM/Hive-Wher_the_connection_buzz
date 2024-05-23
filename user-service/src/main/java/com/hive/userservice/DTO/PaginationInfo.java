package com.hive.userservice.DTO;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class PaginationInfo {
    private List<UserDTO> contents; //1
    private Integer pageNo; //2
    private Integer pageSize; //3
    private Long totalElements; //4
    private Integer totalPages; //5
    private Boolean isLast; //6
    private Boolean hasNext; //7
}