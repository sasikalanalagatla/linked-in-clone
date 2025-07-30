package com.org.linkedin.service.impl;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.ChatMessage;
import com.org.linkedin.model.User;
import com.org.linkedin.repository.ChatMessageRepository;
import com.org.linkedin.service.ChatService;
import com.org.linkedin.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserService userService;

    public ChatServiceImpl(ChatMessageRepository chatMessageRepository, UserService userService) {
        this.chatMessageRepository = chatMessageRepository;
        this.userService = userService;
    }

    @Override
    public void saveMessage(ChatMessage chatMessage) {
        if (chatMessage == null || chatMessage.getSenderEmail() == null || chatMessage.getReceiverEmail() == null) {
            throw new CustomException("INVALID_MESSAGE", "Chat message or sender/receiver email cannot be null");
        }

        User sender = userService.findByEmail(chatMessage.getSenderEmail());
        User receiver = userService.findByEmail(chatMessage.getReceiverEmail());

        chatMessage.setSender(sender);
        chatMessage.setReceiver(receiver);
        chatMessage.setTimestamp(LocalDateTime.now());

        chatMessageRepository.save(chatMessage);
    }

    @Override
    public List<ChatMessage> getChatHistory(String user1Email, String user2Email) {
        if (user1Email == null || user2Email == null) {
            throw new CustomException("INVALID_EMAIL", "User emails cannot be null");
        }

        List<ChatMessage> list1 = chatMessageRepository.findBySenderEmailAndReceiverEmailOrderByTimestamp(user1Email, user2Email);
        List<ChatMessage> list2 = chatMessageRepository.findBySenderEmailAndReceiverEmailOrderByTimestamp(user2Email, user1Email);

        populateUserObjects(list1);
        populateUserObjects(list2);

        list1.addAll(list2);
        list1.sort((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()));
        return list1 != null ? list1 : new ArrayList<>();
    }

    @Override
    public void processMessage(ChatMessage chatMessage, SimpMessagingTemplate messagingTemplate) {
        if (chatMessage == null || chatMessage.getReceiverEmail() == null) {
            throw new CustomException("INVALID_MESSAGE", "Message or receiver email cannot be null");
        }
        saveMessage(chatMessage);
        String to = "/topic/messages/" + chatMessage.getReceiverEmail();
        messagingTemplate.convertAndSend(to, chatMessage);
    }

    @Override
    public void sendVideoCallRequest(String receiverEmail, Principal principal, SimpMessagingTemplate messagingTemplate) {
        if (principal == null) {
            throw new CustomException("UNAUTHORIZED", "User must be logged in");
        }
        if (receiverEmail == null) {
            throw new CustomException("INVALID_EMAIL", "Receiver email cannot be null");
        }

        User sender = userService.findByEmail(principal.getName());
        User receiver = userService.findByEmail(receiverEmail);

        ChatMessage callMessage = new ChatMessage();
        callMessage.setSender(sender);
        callMessage.setReceiver(receiver);
        callMessage.setSenderEmail(sender.getEmail());
        callMessage.setReceiverEmail(receiverEmail);
        callMessage.setContent("📹 Video call request");
        callMessage.setType("video_call_request");
        callMessage.setTimestamp(LocalDateTime.now());

        saveMessage(callMessage);
        messagingTemplate.convertAndSend("/topic/messages/" + receiverEmail, callMessage);
    }

    private void populateUserObjects(List<ChatMessage> messages) {
        for (ChatMessage msg : messages) {
            if (msg.getSender() == null && msg.getSenderEmail() != null) {
                try {
                    User sender = userService.findByEmail(msg.getSenderEmail());
                    msg.setSender(sender);
                } catch (Exception e) {
                    System.err.println("Could not find sender for email: " + msg.getSenderEmail());
                }
            }
            if (msg.getReceiver() == null && msg.getReceiverEmail() != null) {
                try {
                    User receiver = userService.findByEmail(msg.getReceiverEmail());
                    msg.setReceiver(receiver);
                } catch (Exception e) {
                    System.err.println("Could not find receiver for email: " + msg.getReceiverEmail());
                }
            }
        }
    }
}