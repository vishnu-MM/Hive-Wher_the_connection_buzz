package com.hive.userservice.DTO;

import com.hive.userservice.Utility.BlockType;
import com.hive.userservice.Utility.DateFilter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterDTO {
    private BlockType block;
    private DateFilter time;
    private Date startingDate;
    private Date endingDate;
    private Integer pageNo;
    private Integer pageSize;
}