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
import java.util.Optional;

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

        Optional<User> senderOpt = userRepository.findByEmail(chatMessage.getSenderEmail());
        if (!senderOpt.isPresent()) {
            throw new CustomException("USER_NOT_FOUND", "Sender with email " + chatMessage.getSenderEmail() + " not found");
        }

        Optional<User> receiverOpt = userRepository.findByEmail(chatMessage.getReceiverEmail());
        if (!receiverOpt.isPresent()) {
            throw new CustomException("USER_NOT_FOUND", "Receiver with email " + chatMessage.getReceiverEmail() + " not found");
        }

        chatMessage.setSender(senderOpt.get());
        chatMessage.setReceiver(receiverOpt.get());
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

        // Ensure all messages have proper sender/receiver objects populated
        populateUserObjects(list1);
        populateUserObjects(list2);

        list1.addAll(list2);
        list1.sort((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()));
        return list1;
    }

    private void populateUserObjects(List<ChatMessage> messages) {
        for (ChatMessage msg : messages) {
            if (msg.getSender() == null && msg.getSenderEmail() != null) {
                Optional<User> senderOpt = userRepository.findByEmail(msg.getSenderEmail());
                if (senderOpt.isPresent()) {
                    msg.setSender(senderOpt.get());
                }
            }
            if (msg.getReceiver() == null && msg.getReceiverEmail() != null) {
                Optional<User> receiverOpt = userRepository.findByEmail(msg.getReceiverEmail());
                if (receiverOpt.isPresent()) {
                    msg.setReceiver(receiverOpt.get());
                }
            }
        }
    }
}