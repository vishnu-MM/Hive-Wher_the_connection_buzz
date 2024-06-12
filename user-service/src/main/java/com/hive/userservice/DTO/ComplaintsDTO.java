package com.hive.userservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComplaintsDTO {
    private Long id;
    private Long senderId;
    private Long reportedUser;
    private Date date;
    private String description;
}