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

        Optional<User> user = userRepository.findById(Long.parseLong(message.getSenderId()));
        User sender = user.get();
        if (sender != null) {
            message.setSenderName(sender.getFullName());
        }

        // Send video signal to the specific user
        messagingTemplate.convertAndSendToUser(
                message.getReceiverId(),
                "/queue/video",
                message
        );

        System.out.println("Video signal sent to user: " + message.getReceiverId());
    }

    @MessageMapping("/call.notify")
    public void handleCallNotification(CallNotificationMessage message) {
        System.out.println("Received call notification: " + message.getType() + " from " + message.getSenderId() + " to " + message.getReceiverId());



        Optional<User> user = userRepository.findById(Long.parseLong(message.getSenderId()));
        User sender = user.get();

        Optional<User> optionalUser = userRepository.findById(Long.parseLong(message.getSenderId()));
        User receiver = optionalUser.get();

        if (sender != null && receiver != null) {
            System.out.println("Sender: " + sender.getFullName() + ", Receiver: " + receiver.getFullName());

            message.setSenderName(sender.getFullName());

            // Create and save a chat message for the video call request
            ChatMessage callMessage = new ChatMessage();
            callMessage.setSender(sender);
            callMessage.setReceiver(receiver);
            callMessage.setSenderEmail(sender.getEmail());
            callMessage.setReceiverEmail(receiver.getEmail());
            callMessage.setContent("📹 Video call request");
            callMessage.setType("video_call_request");
            callMessage.setTimestamp(LocalDateTime.now());

            // Save to database
            chatService.saveMessage(callMessage);
            System.out.println("Video call message saved to database");

            // Send real-time notification to receiver via regular message channel
            messagingTemplate.convertAndSend("/topic/messages/" + receiver.getEmail(), callMessage);
            System.out.println("Call message sent to topic: /topic/messages/" + receiver.getEmail());

            // Also send direct call notification
            messagingTemplate.convertAndSendToUser(
                    message.getReceiverId(),
                    "/queue/call",
                    message
            );
            System.out.println("Direct call notification sent to user: " + message.getReceiverId());
        } else {
            System.out.println("Error: Could not find sender or receiver");
            if (sender == null) System.out.println("Sender not found for ID: " + message.getSenderId());
            if (receiver == null) System.out.println("Receiver not found for ID: " + message.getReceiverId());
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