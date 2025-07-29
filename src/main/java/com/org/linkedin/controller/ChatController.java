package com.org.linkedin.controller;

import com.org.linkedin.model.ChatMessage;
import com.org.linkedin.service.ChatService;
import com.org.linkedin.service.impl.ConnectionRequestImpl;
import com.org.linkedin.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private ConnectionRequestImpl connectionRequest;

    @MessageMapping("/chat")
    public void processMessage(ChatMessage chatMessage) {
        chatService.processMessage(chatMessage, messagingTemplate);
    }

    @GetMapping
    public String chatPage(@RequestParam(value = "receiverEmail", required = false) String receiverEmail,
                           Principal principal, Model model) {
        return chatService.chatPage(receiverEmail, principal, model, userService, connectionRequest);
    }

    @PostMapping("/send")
    public String sendMessage(@RequestParam String receiverEmail,
                              @RequestParam Long senderId,
                              @RequestParam String content,
                              Model model) {
        return chatService.sendMessage(receiverEmail, senderId, content, model, userService, messagingTemplate);
    }

    @PostMapping("/chat/video-call-request")
    public String sendVideoCallRequest(@RequestParam String receiverEmail,
                                       @RequestParam String senderId,
                                       Principal principal) {
        return chatService.sendVideoCallRequest(receiverEmail, senderId, principal, userService, messagingTemplate);
    }
}
