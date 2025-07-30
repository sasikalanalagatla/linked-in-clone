package com.org.linkedin.controller;

import com.org.linkedin.dto.VideoSignalMessage;
import com.org.linkedin.dto.CallNotificationMessage;
import com.org.linkedin.model.User;
import com.org.linkedin.model.ChatMessage;
import com.org.linkedin.service.impl.UserServiceImpl;
import com.org.linkedin.service.impl.ChatServiceImpl;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;

@Controller
public class VideoCallController {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserServiceImpl userService;
    private final ChatServiceImpl chatService;

    public VideoCallController(SimpMessagingTemplate messagingTemplate,
                               UserServiceImpl userService,
                               ChatServiceImpl chatService) {
        this.messagingTemplate = messagingTemplate;
        this.userService = userService;
        this.chatService = chatService;
    }

    @MessageMapping("/video.signal")
    public void handleVideoSignal(VideoSignalMessage message) {
        if (!"call_request".equals(message.getType())) {
            try {
                User sender = userService.getUserById(Long.parseLong(message.getSenderId()));
                message.setSenderName(sender.getFullName());

                messagingTemplate.convertAndSendToUser(
                        message.getReceiverId(),
                        "/queue/video",
                        message
                );
            } catch (Exception e) {
            }
        }
    }

    @MessageMapping("/call.notify")
    public void handleCallNotification(CallNotificationMessage message) {
        try {
            User sender = userService.getUserById(Long.parseLong(message.getSenderId()));
            User receiver = userService.getUserById(Long.parseLong(message.getReceiverId()));

            message.setSenderName(sender.getFullName());

            messagingTemplate.convertAndSendToUser(
                    message.getReceiverId(),
                    "/queue/call",
                    message
            );

            if ("call_request".equals(message.getType())) {
                ChatMessage callMessage = new ChatMessage();
                callMessage.setContent("📞 Incoming video call");
                callMessage.setType("video_call_request");
                chatService.saveMessage(callMessage);
            }
        } catch (Exception e) {
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