package com.org.linkedin.controller;

import com.org.linkedin.model.ChatMessage;
import com.org.linkedin.model.User;
import com.org.linkedin.service.ChatService;
import com.org.linkedin.service.impl.ConnectionRequestImpl;
import com.org.linkedin.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private ConnectionRequestImpl connectionRequest;

    @MessageMapping("/chat")
    public void processMessage(ChatMessage chatMessage) {
        chatService.saveMessage(chatMessage);
        String to = "/topic/messages/" + chatMessage.getReceiverEmail();
        messagingTemplate.convertAndSend(to, chatMessage);
    }

    @GetMapping
    public String chatPage(@RequestParam(value = "receiverEmail", required = false) String receiverEmail,
                           HttpSession session,
                           Model model) {
        User loggedInUser = userService.getUserById(1L); // use session if available
        String senderEmail = loggedInUser.getEmail();

        List<User> connections = connectionRequest.getConnections(loggedInUser);
        model.addAttribute("connections", connections);
        model.addAttribute("senderEmail", senderEmail);

        if (receiverEmail != null) {
            model.addAttribute("receiverEmail", receiverEmail);
            List<ChatMessage> history = chatService.getChatHistory(senderEmail, receiverEmail);
            model.addAttribute("chatHistory", history);
        }

        return "chat";
    }
}
