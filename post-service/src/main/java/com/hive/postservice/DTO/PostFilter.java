package com.hive.postservice.DTO;

import com.hive.postservice.Utility.DateFilter;
import com.hive.postservice.Utility.PostTypeFilter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostFilter {
    private DateFilter dateFilter;
    private PostTypeFilter postFile;
    private Date startingDate;
    private Date endingDate;
    private Integer pageNo;
    private Integer pageSize;
}