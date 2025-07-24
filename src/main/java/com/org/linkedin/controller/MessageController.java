package com.org.linkedin.controller;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.Message;
import com.org.linkedin.model.User;
import com.org.linkedin.service.MessageService;
import com.org.linkedin.service.impl.ConnectionRequestImpl;
import com.org.linkedin.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private ConnectionRequestImpl connectionRequest;

    @MessageMapping("/message")
    public void processMessage(Message message) {
        if (message == null || message.getReceiver() == null) {
            throw new CustomException("INVALID_MESSAGE", "Message or receiver cannot be null");
        }
        messageService.saveMessage(message);
        String to = "/topic/messages/" + message.getReceiver().getEmail();
        messagingTemplate.convertAndSend(to, message);
    }

    @GetMapping
    public String messagePage(@RequestParam(value = "receiverEmail", required = false) String receiverEmail, Model model) {
        User loggedInUser = userService.getUserById(5L);
        if (loggedInUser == null || loggedInUser.getEmail() == null) {
            throw new CustomException("INVALID_USER", "Logged-in user or email cannot be null");
        }

        List<User> connections = connectionRequest.getConnections(loggedInUser);
        model.addAttribute("connections", connections != null ? connections : new ArrayList<>());
        model.addAttribute("sender", loggedInUser);

        if (receiverEmail != null) {
            model.addAttribute("receiverEmail", receiverEmail);
            User receiver = userService.findByEmail(receiverEmail);
            if (receiver == null) {
                throw new CustomException("USER_NOT_FOUND", "Receiver with email " + receiverEmail + " not found");
            }
            List<Message> history = messageService.getChatHistory(loggedInUser, receiver);
            model.addAttribute("chatHistory", history != null ? history : new ArrayList<>());
        }

        return "message";
    }

    @PostMapping("/send")
    public String sendMessage(
            @RequestParam("senderId") Long senderId,
            @RequestParam("receiverEmail") String receiverEmail,
            @RequestParam("content") String content,
            Model model) {
        if (content == null || content.trim().isEmpty()) {
            model.addAttribute("errorMessage", "Message content cannot be empty");
            return messagePage(receiverEmail, model);
        }

        User sender = userService.getUserById(senderId);
        if (sender == null) {
            throw new CustomException("USER_NOT_FOUND", "Sender with ID " + senderId + " not found");
        }

        User receiver = userService.findByEmail(receiverEmail);
        if (receiver == null) {
            throw new CustomException("USER_NOT_FOUND", "Receiver with email " + receiverEmail + " not found");
        }

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);

        messageService.saveMessage(message);

        // Redirect to refresh the chat history
        return "redirect:/message?receiverEmail=" + receiverEmail;
    }
}