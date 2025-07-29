package com.org.linkedin.service.impl;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.ChatMessage;
import com.org.linkedin.model.User;
import com.org.linkedin.repository.ChatMessageRepository;
import com.org.linkedin.repository.UserRepository;
import com.org.linkedin.service.ChatService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    public ChatServiceImpl(ChatMessageRepository chatMessageRepository, UserRepository userRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void saveMessage(ChatMessage chatMessage) {
        if (chatMessage == null || chatMessage.getSenderEmail() == null || chatMessage.getReceiverEmail() == null) {
            throw new CustomException("INVALID_MESSAGE", "Chat message or sender/receiver email cannot be null");
        }

        Optional<User> senderOpt = userRepository.findByEmail(chatMessage.getSenderEmail());
        if (senderOpt.isEmpty()) {
            throw new CustomException("USER_NOT_FOUND", "Sender with email " + chatMessage.getSenderEmail() + " not found");
        }

        Optional<User> receiverOpt = userRepository.findByEmail(chatMessage.getReceiverEmail());
        if (receiverOpt.isEmpty()) {
            throw new CustomException("USER_NOT_FOUND", "Receiver with email " + chatMessage.getReceiverEmail() + " not found");
        }

        chatMessage.setSender(senderOpt.get());
        chatMessage.setReceiver(receiverOpt.get());
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
        return list1;
    }

    public void processMessage(ChatMessage chatMessage, SimpMessagingTemplate messagingTemplate) {
        if (chatMessage == null || chatMessage.getReceiverEmail() == null) {
            throw new CustomException("INVALID_MESSAGE", "Message or receiver email cannot be null");
        }
        saveMessage(chatMessage);
        String to = "/topic/messages/" + chatMessage.getReceiverEmail();
        messagingTemplate.convertAndSend(to, chatMessage);
    }

    public String chatPage(String receiverEmail, Principal principal, Model model, UserServiceImpl userService, ConnectionRequestImpl connectionRequest) {
        try {
            User loggedInUser = userService.findByEmail(principal.getName());
            String senderEmail = loggedInUser.getEmail();
            if (senderEmail == null) {
                throw new CustomException("INVALID_EMAIL", "Sender email cannot be null");
            }

            List<User> connections = connectionRequest.getConnections(loggedInUser);
            model.addAttribute("connections", connections != null ? connections : new ArrayList<>());
            model.addAttribute("senderEmail", senderEmail);
            model.addAttribute("sender", loggedInUser);

            if (receiverEmail != null && !receiverEmail.trim().isEmpty()) {
                model.addAttribute("receiverEmail", receiverEmail);

                User receiver = userService.findByEmail(receiverEmail);
                model.addAttribute("receiver", receiver);

                List<ChatMessage> history = getChatHistory(senderEmail, receiverEmail);
                if (history != null) {
                    for (ChatMessage msg : history) {
                        if (msg.getSender() == null && msg.getSenderEmail() != null) {
                            try {
                                msg.setSender(userService.findByEmail(msg.getSenderEmail()));
                            } catch (Exception e) {
                                System.err.println("Could not find sender for email: " + msg.getSenderEmail());
                            }
                        }
                        if (msg.getReceiver() == null && msg.getReceiverEmail() != null) {
                            try {
                                msg.setReceiver(userService.findByEmail(msg.getReceiverEmail()));
                            } catch (Exception e) {
                                System.err.println("Could not find receiver for email: " + msg.getReceiverEmail());
                            }
                        }
                    }
                }

                model.addAttribute("chatHistory", history != null ? history : new ArrayList<>());
            }

            return "chat";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error loading chat: " + e.getMessage());
            model.addAttribute("connections", new ArrayList<>());
            model.addAttribute("chatHistory", new ArrayList<>());
            return "chat";
        }
    }

    public String sendMessage(String receiverEmail, Long senderId, String content, Model model, UserServiceImpl userService, SimpMessagingTemplate messagingTemplate) {
        try {
            if (content == null || content.trim().isEmpty()) {
                model.addAttribute("errorMessage", "Message content cannot be empty");
                return "redirect:/chat?receiverEmail=" + receiverEmail;
            }

            User sender = userService.getUserById(senderId);
            User receiver = userService.findByEmail(receiverEmail);

            if (sender == null || receiver == null) {
                model.addAttribute("errorMessage", "Invalid sender or receiver");
                return "redirect:/chat?receiverEmail=" + receiverEmail;
            }

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setSenderEmail(sender.getEmail());
            chatMessage.setReceiverEmail(receiverEmail);
            chatMessage.setContent(content.trim());
            chatMessage.setSender(sender);
            chatMessage.setReceiver(receiver);
            chatMessage.setTimestamp(java.time.LocalDateTime.now());

            saveMessage(chatMessage);

            String to = "/topic/messages/" + receiverEmail;
            messagingTemplate.convertAndSend(to, chatMessage);

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to send message: " + e.getMessage());
        }

        return "redirect:/chat?receiverEmail=" + receiverEmail;
    }

    public String sendVideoCallRequest(String receiverEmail, String senderId, Principal principal, UserServiceImpl userService, SimpMessagingTemplate messagingTemplate) {
        String senderEmail = principal.getName();
        User sender = userService.findByEmail(senderEmail);
        User receiver = userService.findByEmail(receiverEmail);

        if (receiver != null) {
            ChatMessage callMessage = new ChatMessage();
            callMessage.setSender(sender);
            callMessage.setReceiver(receiver);
            callMessage.setContent("📹 Video call request");
            callMessage.setType("video_call_request");

            saveMessage(callMessage);

            messagingTemplate.convertAndSend("/topic/messages/" + receiverEmail, callMessage);
        }

        return "redirect:/chat?receiverEmail=" + receiverEmail;
    }

    private void populateUserObjects(List<ChatMessage> messages) {
        for (ChatMessage msg : messages) {
            if (msg.getSender() == null && msg.getSenderEmail() != null) {
                userRepository.findByEmail(msg.getSenderEmail()).ifPresent(msg::setSender);
            }
            if (msg.getReceiver() == null && msg.getReceiverEmail() != null) {
                userRepository.findByEmail(msg.getReceiverEmail()).ifPresent(msg::setReceiver);
            }
        }
    }
}
