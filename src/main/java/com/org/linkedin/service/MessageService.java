package com.org.linkedin.service;

import com.org.linkedin.model.Message;
import com.org.linkedin.model.User;
import java.util.List;

public interface MessageService {
    Message saveMessage(Message message);
    List<Message> getChatHistory(User user1, User user2);
}