package com.org.linkedin.service;

import com.org.linkedin.model.ChatMessage;
import java.util.List;

public interface ChatService {
    ChatMessage saveMessage(ChatMessage chatMessage);
    List<ChatMessage> getChatHistory(String user1Email, String user2Email);
}