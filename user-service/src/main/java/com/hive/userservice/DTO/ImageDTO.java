package com.hive.userservice.DTO;

import com.hive.userservice.Utility.ImageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageDTO {
    private Long id;
    private String name;
    private String type;
    private byte[] image;
    private ImageType imageType;
    private Long userID;
}