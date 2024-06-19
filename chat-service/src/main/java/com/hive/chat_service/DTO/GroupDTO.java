package com.hive.chat_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupDTO {
    private String id;
    private String groupName;
    private List<String> membersId;
    private byte[] imageData;
    private String imageName;
    private String imageType;
}
