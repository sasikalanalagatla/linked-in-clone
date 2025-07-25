package com.org.linkedin.controller;

import com.org.linkedin.configuration.VideoSignalMessage;
import com.org.linkedin.configuration.CallNotificationMessage;
import com.org.linkedin.model.User;
import com.org.linkedin.model.ChatMessage;
import com.org.linkedin.repository.UserRepository;
import com.org.linkedin.service.impl.UserServiceImpl;
import com.org.linkedin.service.impl.ChatServiceImpl;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class VideoCallController {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserServiceImpl userService;
    private final ChatServiceImpl chatService;
    private final UserRepository userRepository;

    public VideoCallController(SimpMessagingTemplate messagingTemplate,
                               UserServiceImpl userService,
                               ChatServiceImpl chatService, UserRepository userRepository) {
        this.messagingTemplate = messagingTemplate;
        this.userService = userService;
        this.chatService = chatService;
        this.userRepository = userRepository;
    }

    @MessageMapping("/video.signal")
    public void handleVideoSignal(VideoSignalMessage message) {
        System.out.println("Received video signal: " + message.getType() + " from " + message.getSenderId() + " to " + message.getReceiverId());

        Optional<User> senderOptional = userRepository.findById(Long.parseLong(message.getSenderId()));
        if (senderOptional.isPresent()) {
            message.setSenderName(senderOptional.get().getFullName());
        }

        messagingTemplate.convertAndSendToUser(
                message.getReceiverId(),
                "/queue/video",
                message
        );
    }

    @MessageMapping("/call.notify")
    public void handleCallNotification(CallNotificationMessage message) {
        System.out.println("Received call notification: " + message.getType() + " from " + message.getSenderId() + " to " + message.getReceiverId());

        Optional<User> senderOptional = userRepository.findById(Long.parseLong(message.getSenderId()));
        Optional<User> receiverOptional = userRepository.findById(Long.parseLong(message.getReceiverId()));

        if (senderOptional.isPresent() && receiverOptional.isPresent()) {
            User sender = senderOptional.get();
            User receiver = receiverOptional.get();

            message.setSenderName(sender.getFullName());

            // Send call notification to receiver
            messagingTemplate.convertAndSendToUser(
                    message.getReceiverId(),
                    "/queue/call",
                    message
            );

            if ("call_request".equals(message.getType())) {
                ChatMessage callMessage = new ChatMessage();
                callMessage.setSender(sender);
                callMessage.setReceiver(receiver);
                callMessage.setSenderEmail(sender.getEmail());
                callMessage.setReceiverEmail(receiver.getEmail());
                callMessage.setContent("📹 Video call request");
                callMessage.setType("video_call_request");
                callMessage.setTimestamp(LocalDateTime.now());
                chatService.saveMessage(callMessage);

                messagingTemplate.convertAndSend("/topic/messages/" + receiver.getEmail(), callMessage);
            }
        }
    }

    @GetMapping("/video-call/{receiverId}")
    public String showVideoCallPage(@PathVariable String receiverId, Model model, Principal principal) {
        String email = principal.getName();
        User currentUser = userService.findByEmail(email);
        Long currentUserId = currentUser.getUserId();
        model.addAttribute("currentUserId", currentUserId);
        model.addAttribute("receiverId", receiverId);
        return "Video-call";
    }
}