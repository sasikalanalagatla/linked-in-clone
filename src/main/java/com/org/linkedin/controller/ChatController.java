package com.org.linkedin.controller;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.ChatMessage;
import com.org.linkedin.model.User;
import com.org.linkedin.service.ChatService;
import com.org.linkedin.service.UserService;
import com.org.linkedin.service.impl.ConnectionRequestImpl;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;
    private final ConnectionRequestImpl connectionRequest;

    public ChatController(ChatService chatService, SimpMessagingTemplate messagingTemplate,
                          UserService userService, ConnectionRequestImpl connectionRequest) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
        this.userService = userService;
        this.connectionRequest = connectionRequest;
    }

    @MessageMapping("/chat")
    public void processMessage(ChatMessage chatMessage) {
        chatService.processMessage(chatMessage, messagingTemplate);
    }

    @GetMapping
    public String chatPage(@RequestParam(value = "receiverEmail", required = false) String receiverEmail,
                           Principal principal, Model model) {
        try {
            if (principal == null) {
                throw new CustomException("UNAUTHORIZED", "User must be logged in");
            }
            User loggedInUser = userService.findByEmail(principal.getName());
            String senderEmail = loggedInUser.getEmail();
            if (senderEmail == null) {
                throw new CustomException("INVALID_EMAIL", "Sender email cannot be null");
            }

            List<User> connections = connectionRequest.getConnections(loggedInUser);
            model.addAttribute("connections", connections != null ? connections : new ArrayList<>());
            model.addAttribute("senderEmail", senderEmail);
            model.addAttribute("sender", loggedInUser);

            if (receiverEmail != null && !receiverEmail.trim().isEmpty()) {
                model.addAttribute("receiverEmail", receiverEmail);
                User receiver = userService.findByEmail(receiverEmail);
                model.addAttribute("receiver", receiver);
                List<ChatMessage> history = chatService.getChatHistory(senderEmail, receiverEmail);
                model.addAttribute("chatHistory", history != null ? history : new ArrayList<>());
            } else {
                model.addAttribute("chatHistory", new ArrayList<>());
            }

            return "chat";
        } catch (CustomException e) {
            model.addAttribute("errorMessage", "Error loading chat: " + e.getMessage());
            model.addAttribute("connections", new ArrayList<>());
            model.addAttribute("chatHistory", new ArrayList<>());
            return "chat";
        }
    }

    @PostMapping("/send")
    public String sendMessage(@RequestParam String receiverEmail,
                              @RequestParam Long senderId,
                              @RequestParam String content,
                              Model model) {
        try {
            if (content == null || content.trim().isEmpty()) {
                model.addAttribute("errorMessage", "Message content cannot be empty");
                return "redirect:/chat?receiverEmail=" + receiverEmail;
            }

            User sender = userService.getUserById(senderId);
            User receiver = userService.findByEmail(receiverEmail);

            if (sender == null || receiver == null) {
                model.addAttribute("errorMessage", "Invalid sender or receiver");
                return "redirect:/chat?receiverEmail=" + receiverEmail;
            }

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setSenderEmail(sender.getEmail());
            chatMessage.setReceiverEmail(receiverEmail);
            chatMessage.setContent(content.trim());
            chatMessage.setSender(sender);
            chatMessage.setReceiver(receiver);
            chatMessage.setTimestamp(java.time.LocalDateTime.now());

            chatService.saveMessage(chatMessage);
            messagingTemplate.convertAndSend("/topic/messages/" + receiverEmail, chatMessage);

            return "redirect:/chat?receiverEmail=" + receiverEmail;
        } catch (CustomException e) {
            model.addAttribute("errorMessage", "Failed to send message: " + e.getMessage());
            return "redirect:/chat?receiverEmail=" + receiverEmail;
        }
    }

    @PostMapping("/video-call-request")
    public String sendVideoCallRequest(@RequestParam String receiverEmail,
                                       @RequestParam String senderId,
                                       Principal principal) {
        try {
            chatService.sendVideoCallRequest(receiverEmail, principal, messagingTemplate);
            return "redirect:/chat?receiverEmail=" + receiverEmail;
        } catch (CustomException e) {
            return "redirect:/chat?receiverEmail=" + receiverEmail + "&error=" + e.getMessage();
        }
    }
}