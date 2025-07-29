package com.org.linkedin.service;

import com.org.linkedin.model.ChatMessage;
import com.org.linkedin.service.impl.ConnectionRequestImpl;
import com.org.linkedin.service.impl.UserServiceImpl;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.List;

public interface ChatService {
    void saveMessage(ChatMessage chatMessage);
    List<ChatMessage> getChatHistory(String user1Email, String user2Email);
    void processMessage(ChatMessage chatMessage, SimpMessagingTemplate messagingTemplate);
    String chatPage(String receiverEmail, Principal principal, Model model,
                    UserServiceImpl userService, ConnectionRequestImpl connectionRequest);

    String sendVideoCallRequest(String receiverEmail, String senderId, Principal principal,
                                UserServiceImpl userService, SimpMessagingTemplate messagingTemplate);

    String sendMessage(String receiverEmail, Long senderId, String content, Model model, UserServiceImpl
            userService, SimpMessagingTemplate messagingTemplate);
}