package com.hive.postservice.DTO;

import com.hive.postservice.Utility.PostType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDTO {
    private String description;
    private Long userId;
    private PostType postType;
}
