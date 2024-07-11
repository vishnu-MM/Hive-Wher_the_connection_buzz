package com.hive.chat_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OnlineUpdate {
    private Long userId;
    private List<Long> friendList;
    private Boolean isOnline;
}