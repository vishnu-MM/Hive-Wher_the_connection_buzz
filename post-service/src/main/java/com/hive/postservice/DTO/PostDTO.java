package com.hive.postservice.DTO;

import com.hive.postservice.Utility.PostType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {
    private Long id;
    private String description;
    private String fileName;
    private String fileType;
    private String filePath;
    private Timestamp createdOn;
    private Long userId;
    private Boolean isBlocked;
    private PostType postType;
}