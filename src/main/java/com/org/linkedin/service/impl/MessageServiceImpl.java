package com.org.linkedin.service.impl;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.Message;
import com.org.linkedin.model.User;
import com.org.linkedin.repository.MessageRepository;
import com.org.linkedin.repository.UserRepository;
import com.org.linkedin.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Message saveMessage(Message message) {
        if (message == null || message.getSender() == null || message.getReceiver() == null) {
            throw new CustomException("INVALID_MESSAGE", "Message or sender/receiver cannot be null");
        }
        message.setTimestamp(LocalDateTime.now());
        return messageRepository.save(message);
    }

    @Override
    public List<Message> getChatHistory(User user1, User user2) {
        if (user1 == null || user2 == null) {
            throw new CustomException("INVALID_USER", "User objects cannot be null");
        }
        List<Message> list1 = messageRepository.findBySenderAndReceiverOrderByTimestamp(user1, user2);
        List<Message> list2 = messageRepository.findBySenderAndReceiverOrderByTimestamp(user2, user1);
        List<Message> combined = new ArrayList<>();
        combined.addAll(list1);
        combined.addAll(list2);

        // Manual deduplication based on id
        List<Message> uniqueMessages = new ArrayList<>();
        for (Message msg : combined) {
            boolean isDuplicate = false;
            for (Message uniqueMsg : uniqueMessages) {
                if (uniqueMsg.getId() != null && uniqueMsg.getId().equals(msg.getId())) {
                    isDuplicate = true;
                    break;
                }
            }
            if (!isDuplicate && msg.getId() != null) {
                uniqueMessages.add(msg);
            }
        }

        // Sort manually by timestamp
        for (int i = 0; i < uniqueMessages.size() - 1; i++) {
            for (int j = i + 1; j < uniqueMessages.size(); j++) {
                if (uniqueMessages.get(i).getTimestamp().compareTo(uniqueMessages.get(j).getTimestamp()) > 0) {
                    Message temp = uniqueMessages.get(i);
                    uniqueMessages.set(i, uniqueMessages.get(j));
                    uniqueMessages.set(j, temp);
                }
            }
        }

        return uniqueMessages;
    }
}