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
    public List<ChatMessage> getChatHistory(String senderEmail, String receiverEmail) {
        if (senderEmail == null || receiverEmail == null) {
            throw new CustomException("INVALID_EMAIL", "User emails cannot be null");
        }

        List<ChatMessage> chatHistory = chatMessageRepository.findChatHistoryBetweenUsers(senderEmail, receiverEmail);

        populateUserObjects(chatHistory);

        return chatHistory != null ? chatHistory : new ArrayList<>();
    }

    @Override
    public List<ChatMessage> getChatHistoryWithPagination(String senderEmail, String receiverEmail,
                                                          int limit, int offset) {
        if (senderEmail == null || receiverEmail == null) {
            throw new CustomException("INVALID_EMAIL", "User emails cannot be null");
        }

        List<ChatMessage> fullHistory = getChatHistory(senderEmail, receiverEmail);

        int startIndex = Math.max(0, offset);
        int endIndex = Math.min(fullHistory.size(), offset + limit);

        if (startIndex >= fullHistory.size()) {
            return new ArrayList<>();
        }

        return fullHistory.subList(startIndex, endIndex);
    }

    @Override
    public List<ChatMessage> getRecentMessagesForUser(String userEmail, int limit) {
        if (userEmail == null) {
            throw new CustomException("INVALID_EMAIL", "User email cannot be null");
        }

        List<ChatMessage> sentMessages = chatMessageRepository.findBySenderEmailOrderByTimestampDesc(userEmail);
        List<ChatMessage> receivedMessages = chatMessageRepository.findByReceiverEmailOrderByTimestampDesc(userEmail);

        List<ChatMessage> allMessages = new ArrayList<>();
        allMessages.addAll(sentMessages);
        allMessages.addAll(receivedMessages);

        populateUserObjects(allMessages);

        List<ChatMessage> recentMessages = new ArrayList<>();
        int size = Math.min(limit, allMessages.size());
        for (int i = 0; i < size; i++) {
            recentMessages.add(allMessages.get(i));
        }

        return recentMessages;
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
    public void sendVideoCallRequest(String receiverEmail, Principal principal,
                                     SimpMessagingTemplate messagingTemplate) {
        if (principal == null) {
            throw new CustomException("UNAUTHORIZED", "User must be logged in");
        }
        if (receiverEmail == null) {
            throw new CustomException("INVALID_EMAIL", "Receiver email cannot be null");
        }

        User sender = userService.findByEmail(principal.getName());
        User receiver = userService.findByEmail(receiverEmail);

        ChatMessage callMessage = createVideoCallMessage(sender, receiver);

        saveMessage(callMessage);
        messagingTemplate.convertAndSend("/topic/messages/" + receiverEmail, callMessage);
    }

    private ChatMessage createVideoCallMessage(User sender, User receiver) {
        ChatMessage callMessage = new ChatMessage();
        callMessage.setSender(sender);
        callMessage.setReceiver(receiver);
        callMessage.setSenderEmail(sender.getEmail());
        callMessage.setReceiverEmail(receiver.getEmail());
        callMessage.setContent("📹 Video call request");
        callMessage.setType("video_call_request");
        callMessage.setTimestamp(LocalDateTime.now());
        return callMessage;
    }

    private void populateUserObjects(List<ChatMessage> messages) {
        if (messages == null || messages.isEmpty()) {
            return;
        }

        for (ChatMessage msg : messages) {
            try {
                if (msg.getSender() == null && msg.getSenderEmail() != null) {
                    User sender = userService.findByEmail(msg.getSenderEmail());
                    msg.setSender(sender);
                }
                if (msg.getReceiver() == null && msg.getReceiverEmail() != null) {
                    User receiver = userService.findByEmail(msg.getReceiverEmail());
                    msg.setReceiver(receiver);
                }
            } catch (Exception e) {
                System.err.println("Error populating user objects for message ID " + msg.getId() + ": " + e.getMessage());
            }
        }
    }
}