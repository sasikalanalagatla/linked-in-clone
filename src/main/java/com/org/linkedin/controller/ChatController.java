package com.org.linkedin.controller;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.ChatMessage;
import com.org.linkedin.model.User;
import com.org.linkedin.service.ChatService;
import com.org.linkedin.service.impl.ConnectionRequestImpl;
import com.org.linkedin.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
        if (chatMessage == null || chatMessage.getReceiverEmail() == null) {
            throw new CustomException("INVALID_MESSAGE", "Message or receiver email cannot be null");
        }
        chatService.saveMessage(chatMessage);
        String to = "/topic/messages/" + chatMessage.getReceiverEmail();
        messagingTemplate.convertAndSend(to, chatMessage);
    }

    @GetMapping
    public String chatPage(@RequestParam(value = "receiverEmail", required = false) String receiverEmail, Model model) {
        User loggedInUser = userService.getUserById(1L); // Hardcoded as per original
        String senderEmail = loggedInUser.getEmail();
        if (senderEmail == null) {
            throw new CustomException("INVALID_EMAIL", "Sender email cannot be null");
        }

        List<User> connections = connectionRequest.getConnections(loggedInUser);
        model.addAttribute("connections", connections != null ? connections : new ArrayList<>());
        model.addAttribute("senderEmail", senderEmail);

        if (receiverEmail != null) {
            model.addAttribute("receiverEmail", receiverEmail);
            List<ChatMessage> history = chatService.getChatHistory(senderEmail, receiverEmail);
            model.addAttribute("chatHistory", history != null ? history : new ArrayList<>());
        }

        return "chat";
    }
}