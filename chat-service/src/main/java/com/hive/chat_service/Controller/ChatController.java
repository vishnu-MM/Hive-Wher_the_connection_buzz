package com.hive.chat_service.Controller;

import com.hive.chat_service.DTO.GroupDTO;
import com.hive.chat_service.DTO.NotificationDTO;
import com.hive.chat_service.DTO.PaginationDTO;
import com.hive.chat_service.Entity.Message;
import com.hive.chat_service.Service.ChatRoomService;
import com.hive.chat_service.Service.GroupService;
import com.hive.chat_service.Service.MessageService;
import com.hive.chat_service.Service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ChatController {
    private static final Logger log = LoggerFactory.getLogger(ChatController.class);
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;
    private final MessageService messageService;
    private final ChatRoomService chatRoomService;
    private final GroupService groupService;

    @MessageMapping("/chat")
    public void processMessage(@Payload Message message) {
        String destination = "/queue/messages";
        Message savedMsg = messageService.save(message);
        messagingTemplate.convertAndSendToUser(
                savedMsg.getRecipientId(),
                destination,
                savedMsg
        );
    }

    @GetMapping("/messages")
    public ResponseEntity<List<Message>> findMessages(@RequestParam("senderId") String senderId,
                                                      @RequestParam("recipientId") String recipientId) {
        return ResponseEntity.ok(messageService.findMessages(senderId, recipientId));
    }

    @GetMapping("/notifications")
    public ResponseEntity<PaginationDTO> findMessages(@RequestParam("userId") Long userId,
                                                      @RequestParam("pageSize") Integer pageSize,
                                                      @RequestParam("pageNo") Integer pageNo) {
        return ResponseEntity.ok(notificationService.findAll(userId, pageNo, pageSize));
    }

    @GetMapping("/get-users")
    public ResponseEntity<List<Long>> findUsers(@RequestParam("userId") Long userId) {
        return ResponseEntity.ok(chatRoomService.getChatUsers(userId));
    }

    @PostMapping("/new-group")
    public ResponseEntity<GroupDTO> createGroup(@RequestBody GroupDTO groupDTO) {
        return ResponseEntity.ok(groupService.createGroup(groupDTO));
    }

    @GetMapping("/groups")
    public ResponseEntity<List<GroupDTO>> createGroup(@RequestParam("userId") String userId) {
        return ResponseEntity.ok(groupService.findAllGroupByUser(userId));
    }
}
