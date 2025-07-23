package com.org.linkedin.controller;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.ConnectionRequest;
import com.org.linkedin.model.User;
import com.org.linkedin.service.ConnectionRequestService;
import com.org.linkedin.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class MyNetworkController {

    private final ConnectionRequestService connectionRequestService;
    private final UserService userService;

    public MyNetworkController(ConnectionRequestService connectionRequestService, UserService userService) {
        this.connectionRequestService = connectionRequestService;
        this.userService = userService;
    }

    @GetMapping("/mynetwork")
    public String showMyNetwork(Model model) {
        User currentUser = userService.getUserById(1L); // Hardcoded as per original
        List<ConnectionRequest> pendingRequests = connectionRequestService.getPendingRequests(currentUser);
        List<User> connections = connectionRequestService.getConnections(currentUser);
        List<User> followers = userService.getFollowers(currentUser);
        List<User> following = userService.getFollowing(currentUser);

        model.addAttribute("requests", pendingRequests != null ? pendingRequests : new ArrayList<>());
        model.addAttribute("connections", connections != null ? connections : new ArrayList<>());
        model.addAttribute("followers", followers != null ? followers : new ArrayList<>());
        model.addAttribute("following", following != null ? following : new ArrayList<>());
        model.addAttribute("currentUserId", currentUser.getUserId());

        return "mynetwork";
    }

    @GetMapping("/profile/{id}/followers")
    public String showFollowers(@PathVariable Long id, Model model) {
        if (id == null) {
            throw new CustomException("INVALID_USER_ID", "User ID cannot be null");
        }
        User user = userService.getUserById(id);
        List<User> followers = userService.getFollowers(user);
        model.addAttribute("followers", followers != null ? followers : new ArrayList<>());
        model.addAttribute("currentUserId", id);
        return "followers";
    }

    @GetMapping("/profile/{id}/following")
    public String showFollowing(@PathVariable Long id, Model model) {
        if (id == null) {
            throw new CustomException("INVALID_USER_ID", "User ID cannot be null");
        }
        User user = userService.getUserById(id);
        List<User> following = userService.getFollowing(user);
        model.addAttribute("following", following != null ? following : new ArrayList<>());
        model.addAttribute("currentUserId", id);
        return "following";
    }

    @PostMapping("/accept/{requestId}")
    public String acceptConnection(@PathVariable Long requestId, Model model) {
        if (requestId == null) {
            throw new CustomException("INVALID_REQUEST_ID", "Request ID cannot be null");
        }
        try {
            connectionRequestService.acceptRequest(requestId);
            return "redirect:/mynetwork";
        } catch (CustomException e) {
            model.addAttribute("error", "Error accepting connection request: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/connect/{receiverId}")
    public String sendConnectionRequest(@PathVariable Long receiverId, Model model) {
        if (receiverId == null) {
            throw new CustomException("INVALID_USER_ID", "Receiver ID cannot be null");
        }
        try {
            User sender = userService.getUserById(1L); // Hardcoded as per original
            User receiver = userService.getUserById(receiverId);
            connectionRequestService.sendRequest(sender, receiver);
            return "redirect:/profile/" + receiverId;
        } catch (CustomException e) {
            model.addAttribute("error", "Error sending connection request: " + e.getMessage());
            return "error";
        }
    }
}