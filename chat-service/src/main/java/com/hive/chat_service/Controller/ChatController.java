package com.hive.chat_service.Controller;

import com.hive.chat_service.DTO.MessageDTO;
import com.hive.chat_service.Entity.Message;
import com.hive.chat_service.Service.MessageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ChatController {
    private static final Logger log = LoggerFactory.getLogger(ChatController.class);
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    @MessageMapping("/chat")
    public void processMessage(@Payload Message message) {
        log.info(message.toString());
        Message savedMsg = messageService.save(message);
        messagingTemplate.convertAndSendToUser(
                message.getRecipientId(), "/queue/messages",
                message
        );
    }

    @GetMapping("/messages")
    public ResponseEntity<List<Message>> findMessages(@RequestParam("senderId") String senderId,
                                                      @RequestParam("recipientId") String recipientId) {
        return ResponseEntity.ok(messageService.findMessages(senderId, recipientId));
    }
}
