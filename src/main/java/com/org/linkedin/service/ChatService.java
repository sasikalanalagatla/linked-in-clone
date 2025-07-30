package com.org.linkedin.service;

import com.org.linkedin.model.ChatMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.security.Principal;
import java.util.List;

public interface ChatService {
    void saveMessage(ChatMessage chatMessage);
    List<ChatMessage> getChatHistory(String user1Email, String user2Email);
    void processMessage(ChatMessage chatMessage, SimpMessagingTemplate messagingTemplate);
    void sendVideoCallRequest(String receiverEmail, Principal principal, SimpMessagingTemplate messagingTemplate);
}