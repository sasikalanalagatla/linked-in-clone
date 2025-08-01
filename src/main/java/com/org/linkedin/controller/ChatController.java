package com.org.linkedin.controller;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.ChatMessage;
import com.org.linkedin.model.User;
import com.org.linkedin.service.ChatService;
import com.org.linkedin.service.ConnectionRequestService;
import com.org.linkedin.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;
    private final ConnectionRequestService connectionRequestService;

    public ChatController(ChatService chatService, SimpMessagingTemplate messagingTemplate,
                          UserService userService, ConnectionRequestService connectionRequest) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
        this.userService = userService;
        this.connectionRequestService = connectionRequest;
    }

    @MessageMapping("/chat")
    public void processMessage(ChatMessage chatMessage) {
        try {
            chatService.processMessage(chatMessage, messagingTemplate);
        } catch (CustomException e) {
            System.err.println("Error processing WebSocket message: " + e.getMessage());
        }
    }

    @GetMapping
    public String chatPage(@RequestParam(value = "receiverEmail", required = false) String receiverEmail,
                           @RequestParam(value = "page", defaultValue = "0") int page,
                           @RequestParam(value = "size", defaultValue = "50") int size,
                           Principal principal, Model model) {
        try {
            validatePrincipal(principal);

            User loggedInUser = userService.findByEmail(principal.getName());
            String senderEmail = loggedInUser.getEmail();

            List<User> connections;
            try {
                connections = connectionRequestService.getConnections(loggedInUser);
                if (connections == null) {
                    connections = new ArrayList<>();
                }
            } catch (Exception e) {
                throw new CustomException("CONNECTION_LOAD_FAILED", "Error loading connections: " + e.getMessage());
            }

            model.addAttribute("connections", connections);
            model.addAttribute("senderEmail", senderEmail);
            model.addAttribute("sender", loggedInUser);

            if (receiverEmail != null && !receiverEmail.trim().isEmpty()) {
                try {
                    model.addAttribute("receiverEmail", receiverEmail);

                    User receiver = userService.findByEmail(receiverEmail);
                    model.addAttribute("receiver", receiver);

                    List<ChatMessage> history;
                    if (page > 0 || size != 50) {
                        int offset = page * size;
                        history = chatService.getChatHistoryWithPagination(senderEmail, receiverEmail, size, offset);
                    } else {
                        history = chatService.getChatHistory(senderEmail, receiverEmail);
                    }

                    model.addAttribute("chatHistory", history != null ? history : new ArrayList<>());
                    model.addAttribute("currentPage", page);
                    model.addAttribute("pageSize", size);

                } catch (Exception e) {
                    throw new CustomException("CHAT_HISTORY_LOAD_FAILED", "Error loading chat data: " + e.getMessage());
                }
            } else {
                model.addAttribute("chatHistory", new ArrayList<>());
                model.addAttribute("currentPage", 0);
                model.addAttribute("pageSize", 50);
            }

            return "chat";

        } catch (CustomException e) {
            return handleChatPageError(e, model);
        } catch (Exception e) {
            return handleChatPageError(new CustomException("SYSTEM_ERROR", "An unexpected error occurred"), model);
        }
    }

    @PostMapping("/send")
    public String sendMessage(@RequestParam String receiverEmail,
                              @RequestParam Long senderId,
                              @RequestParam String content,
                              Model model) {
        try {
            if (content == null || content.trim().isEmpty()) {
                throw new CustomException("INVALID_CONTENT", "Message content cannot be empty");
            }
            if (content.length() > 1000) {
                throw new CustomException("INVALID_CONTENT", "Message content too long");
            }

            User sender = userService.getUserById(senderId);
            User receiver = userService.findByEmail(receiverEmail);

            if (sender == null) {
                throw new CustomException("INVALID_USER", "Sender not found");
            }
            if (receiver == null) {
                throw new CustomException("INVALID_USER", "Receiver not found");
            }

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setSenderEmail(sender.getEmail());
            chatMessage.setReceiverEmail(receiver.getEmail());
            chatMessage.setContent(content.trim());
            chatMessage.setSender(sender);
            chatMessage.setReceiver(receiver);
            chatMessage.setTimestamp(java.time.LocalDateTime.now());

            chatService.saveMessage(chatMessage);

            messagingTemplate.convertAndSend("/topic/messages/" + receiverEmail, chatMessage);

            return "redirect:/chat?receiverEmail=" + receiverEmail;

        } catch (CustomException e) {
            model.addAttribute("errorMessage", "Failed to send message: " + e.getMessage());
            return "redirect:/chat?receiverEmail=" + receiverEmail + "&error=" + e.getMessage();
        }
    }

    @PostMapping("/video-call-request")
    public String sendVideoCallRequest(@RequestParam String receiverEmail,
                                       @RequestParam String senderId,
                                       Principal principal) {
        try {
            validatePrincipal(principal);
            chatService.sendVideoCallRequest(receiverEmail, principal, messagingTemplate);
            return "redirect:/chat?receiverEmail=" + receiverEmail;

        } catch (CustomException e) {
            return "redirect:/chat?receiverEmail=" + receiverEmail + "&error=" + e.getMessage();
        }
    }

    @GetMapping("/api/history")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getChatHistory(
            @RequestParam String receiverEmail,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Principal principal) {

        Map<String, Object> response = new HashMap<>();

        try {
            validatePrincipal(principal);

            String senderEmail = principal.getName();
            int offset = page * size;

            List<ChatMessage> messages = chatService.getChatHistoryWithPagination(senderEmail, receiverEmail, size, offset);

            response.put("messages", messages);
            response.put("page", page);
            response.put("size", size);
            response.put("hasMore", messages.size() == size);
            response.put("success", true);

            return ResponseEntity.ok(response);

        } catch (CustomException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/api/recent")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getRecentMessages(
            @RequestParam(defaultValue = "10") int limit,
            Principal principal) {

        Map<String, Object> response = new HashMap<>();

        try {
            validatePrincipal(principal);

            String userEmail = principal.getName();
            List<ChatMessage> recentMessages = chatService.getRecentMessagesForUser(userEmail, limit);

            response.put("messages", recentMessages);
            response.put("success", true);

            return ResponseEntity.ok(response);

        } catch (CustomException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


    private void validatePrincipal(Principal principal) {
        if (principal == null) {
            throw new CustomException("UNAUTHORIZED", "User must be logged in");
        }
    }

    private String handleChatPageError(CustomException e, Model model) {
        model.addAttribute("errorMessage", "Error loading chat: " + e.getMessage());
        model.addAttribute("connections", new ArrayList<>());
        model.addAttribute("chatHistory", new ArrayList<>());
        model.addAttribute("currentPage", 0);
        model.addAttribute("pageSize", 50);
        return "chat";
    }
}