package com.hive.chat_service.Controller;

import com.hive.chat_service.DTO.GroupDTO;
import com.hive.chat_service.DTO.NotificationDTO;
import com.hive.chat_service.DTO.OnlineUpdate;
import com.hive.chat_service.DTO.PaginationDTO;
import com.hive.chat_service.Entity.Message;
import com.hive.chat_service.Service.*;
import com.hive.chat_service.Utility.MessageType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

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
    private final UserOnlineService uoService;

    @MessageMapping("/chat")
    public void processMessage(@Payload Message message) {
        String destination = "/queue/messages";

        Message savedMsg = messageService.save(message);

        if (savedMsg.getMessageType() == MessageType.PRIVATE) {
            if (!Objects.equals(savedMsg.getSenderId(), savedMsg.getRecipientId())) {
                messagingTemplate.convertAndSendToUser(
                        savedMsg.getRecipientId(),
                        destination,
                        savedMsg
                );
            }
        }
        else if (savedMsg.getMessageType() == MessageType.GROUP) {
            GroupDTO groupDTO = groupService.findById(savedMsg.getRecipientId());
            for (String member : groupDTO.getMembersId()) {
                if(Objects.equals(member, savedMsg.getSenderId()))
                    continue;
                messagingTemplate.convertAndSendToUser(member, destination, savedMsg);
            }
        }
    }

    @MessageMapping("/update")
    public void onlineListUpdation(@Payload OnlineUpdate onlineUpdate) {
        System.out.println(onlineUpdate);
        String destination = "/queue/online";
        for (Long userId : onlineUpdate.getFriendList()) {
            messagingTemplate.convertAndSendToUser(userId.toString(), destination, onlineUpdate);
        }
    }


    //REST APIs

    @GetMapping("/messages")
    public ResponseEntity<List<Message>> findMessages(@RequestParam("senderId") String senderId,
                                                      @RequestParam("recipientId") String recipientId,
                                                      @RequestParam("messageType") MessageType messageType) {
        return ResponseEntity.ok(messageService.findMessages(senderId, recipientId, messageType));
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
    public ResponseEntity<List<GroupDTO>> getGroups(@RequestParam("userId") String userId) {
        return ResponseEntity.ok(groupService.findAllGroupByUser(userId));
    }

    @GetMapping("/group")
    public ResponseEntity<GroupDTO> getGroup(@RequestParam("groupId") String groupId) {
        return ResponseEntity.ok(groupService.findById(groupId));
    }
}
