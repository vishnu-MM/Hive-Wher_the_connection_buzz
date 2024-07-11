package com.hive.chat_service.Service;

import com.hive.chat_service.DTO.OnlineUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
public class UserOnlineService {
    private final SimpMessagingTemplate messagingTemplate;

    @Async
    public Future<Void> processListAsync(OnlineUpdate onlineUpdate) {
        System.out.println(onlineUpdate);
        String destination = "/queue/online";
        for (Long userId : onlineUpdate.getFriendList()) {
            messagingTemplate.convertAndSendToUser(userId.toString(), destination, onlineUpdate);
        }
        return CompletableFuture.completedFuture(null);
    }
}
