package com.hive.postservice.DTO;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class LikeRequestDTO {
    private Long userId;
    private Long postId;
}
