package com.org.linkedin.service.impl;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.ChatMessage;
import com.org.linkedin.model.User;
import com.org.linkedin.repository.ChatMessageRepository;
import com.org.linkedin.repository.UserRepository;
import com.org.linkedin.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ChatMessage saveMessage(ChatMessage chatMessage) {
        if (chatMessage == null || chatMessage.getSenderEmail() == null || chatMessage.getReceiverEmail() == null) {
            throw new CustomException("INVALID_MESSAGE", "Chat message or sender/receiver email cannot be null");
        }
        User sender = userRepository.findByEmail(chatMessage.getSenderEmail());
        if (sender == null) {
            throw new CustomException("USER_NOT_FOUND", "Sender with email " + chatMessage.getSenderEmail() + " not found");
        }
        User receiver = userRepository.findByEmail(chatMessage.getReceiverEmail());
        if (receiver == null) {
            throw new CustomException("USER_NOT_FOUND", "Receiver with email " + chatMessage.getReceiverEmail() + " not found");
        }

        chatMessage.setSender(sender);
        chatMessage.setReceiver(receiver);
        chatMessage.setTimestamp(LocalDateTime.now());

        return chatMessageRepository.save(chatMessage);
    }

    @Override
    public List<ChatMessage> getChatHistory(String user1Email, String user2Email) {
        if (user1Email == null || user2Email == null) {
            throw new CustomException("INVALID_EMAIL", "User emails cannot be null");
        }
        List<ChatMessage> list1 = chatMessageRepository.findBySenderEmailAndReceiverEmailOrderByTimestamp(user1Email, user2Email);
        List<ChatMessage> list2 = chatMessageRepository.findBySenderEmailAndReceiverEmailOrderByTimestamp(user2Email, user1Email);
        list1.addAll(list2);
        list1.sort((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()));
        return list1;
    }
}
