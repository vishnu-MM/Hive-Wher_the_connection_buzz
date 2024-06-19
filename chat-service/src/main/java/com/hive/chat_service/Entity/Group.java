package com.hive.chat_service.Entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@Builder
@Document
@AllArgsConstructor
@NoArgsConstructor
public class Group {
    @Id private String id;
    private String groupName;
    private List<String> membersId;
    private byte[] imageData;
    private String imageName;
    private String imageType;
}