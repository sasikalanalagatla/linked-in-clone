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

import java.security.Principal;
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
    public String chatPage(@RequestParam(value = "receiverEmail", required = false) String receiverEmail,
                           Principal principal, Model model) {
        try {
            User loggedInUser = userService.findByEmail(principal.getName()); // Hardcoded as per original
            String senderEmail = loggedInUser.getEmail();
            if (senderEmail == null) {
                throw new CustomException("INVALID_EMAIL", "Sender email cannot be null");
            }

            List<User> connections = connectionRequest.getConnections(loggedInUser);
            model.addAttribute("connections", connections != null ? connections : new ArrayList<>());
            model.addAttribute("senderEmail", senderEmail);
            model.addAttribute("sender", loggedInUser); // Add sender User object

            if (receiverEmail != null && !receiverEmail.trim().isEmpty()) {
                model.addAttribute("receiverEmail", receiverEmail);

                // Get receiver User object
                User receiver = userService.findByEmail(receiverEmail);
                model.addAttribute("receiver", receiver);

                List<ChatMessage> history = chatService.getChatHistory(senderEmail, receiverEmail);

                // Ensure all messages have proper sender/receiver objects
                if (history != null) {
                    for (ChatMessage msg : history) {
                        if (msg.getSender() == null && msg.getSenderEmail() != null) {
                            try {
                                msg.setSender(userService.findByEmail(msg.getSenderEmail()));
                            } catch (Exception e) {
                                // Handle case where sender might not exist
                                System.err.println("Could not find sender for email: " + msg.getSenderEmail());
                            }
                        }
                        if (msg.getReceiver() == null && msg.getReceiverEmail() != null) {
                            try {
                                msg.setReceiver(userService.findByEmail(msg.getReceiverEmail()));
                            } catch (Exception e) {
                                // Handle case where receiver might not exist
                                System.err.println("Could not find receiver for email: " + msg.getReceiverEmail());
                            }
                        }
                    }
                }

                model.addAttribute("chatHistory", history != null ? history : new ArrayList<>());
            }

            return "chat";
        } catch (Exception e) {
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
            // Validate input
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

            // Send via WebSocket
            String to = "/topic/messages/" + receiverEmail;
            messagingTemplate.convertAndSend(to, chatMessage);

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to send message: " + e.getMessage());
        }

        return "redirect:/chat?receiverEmail=" + receiverEmail;
    }
}