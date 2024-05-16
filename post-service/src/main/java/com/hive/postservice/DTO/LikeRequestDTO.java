package com.hive.postservice.DTO;

import lombok.Data;


@Data
public class LikeRequestDTO {
    private Long userId;
    private Long postId;
}
