package com.org.linkedin.service;

import com.org.linkedin.model.ChatMessage;
import java.util.List;

public interface ChatService {
    void saveMessage(ChatMessage chatMessage);
    List<ChatMessage> getChatHistory(String user1Email, String user2Email);
}