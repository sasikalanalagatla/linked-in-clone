package com.org.linkedin.controller;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.ConnectionRequest;
import com.org.linkedin.model.User;
import com.org.linkedin.service.ConnectionRequestService;
import com.org.linkedin.service.FollowService;
import com.org.linkedin.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MyNetworkController {

    private final ConnectionRequestService connectionRequestService;
    private final UserService userService;
    private final FollowService followService;

    public MyNetworkController(ConnectionRequestService connectionRequestService,
                               UserService userService,
                               FollowService followService) {
        this.connectionRequestService = connectionRequestService;
        this.userService = userService;
        this.followService = followService;
    }

    @GetMapping("/mynetwork")
    public String showMyNetwork(Model model, Principal principal) {
        String email = principal.getName();
        User currentUser = userService.findByEmail(email);
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
    public String acceptConnection(@PathVariable Long requestId, Model model, RedirectAttributes redirectAttributes) {
        if (requestId == null) {
            throw new CustomException("INVALID_REQUEST_ID", "Request ID cannot be null");
        }
        try {
            connectionRequestService.acceptRequest(requestId);
            redirectAttributes.addFlashAttribute("success", "Connection request accepted successfully!");
            return "redirect:/mynetwork";
        } catch (CustomException e) {
            redirectAttributes.addFlashAttribute("error", "Error accepting connection request: " + e.getMessage());
            return "redirect:/mynetwork";
        }
    }

    @PostMapping("/ignore/{requestId}")
    public String ignoreConnection(@PathVariable Long requestId, RedirectAttributes redirectAttributes) {
        if (requestId == null) {
            throw new CustomException("INVALID_REQUEST_ID", "Request ID cannot be null");
        }
        try {
            connectionRequestService.ignoreRequest(requestId);
            redirectAttributes.addFlashAttribute("success", "Connection request ignored successfully!");
            return "redirect:/mynetwork";
        } catch (CustomException e) {
            redirectAttributes.addFlashAttribute("error", "Error ignoring connection request: " + e.getMessage());
            return "redirect:/mynetwork";
        }
    }

    @PostMapping("/connect/{receiverId}")
    public String sendConnectionRequest(@PathVariable Long receiverId, Model model, Principal principal, RedirectAttributes redirectAttributes) {
        String email = principal.getName();
        User user = userService.findByEmail(email);
        if (receiverId == null) {
            throw new CustomException("INVALID_USER_ID", "Receiver ID cannot be null");
        }
        try {
            User sender = userService.getUserById(user.getUserId());
            User receiver = userService.getUserById(receiverId);
            connectionRequestService.sendRequest(sender, receiver);
            redirectAttributes.addFlashAttribute("success", "Connection request sent successfully!");
            return "redirect:/profile/" + receiverId;
        } catch (CustomException e) {
            redirectAttributes.addFlashAttribute("error", "Error sending connection request: " + e.getMessage());
            return "redirect:/profile/" + receiverId;
        }
    }

    @PostMapping("/follow/{userId}")
    public String followUser(@PathVariable Long userId, Principal principal) {
        User currentUser = userService.findByEmail(principal.getName());
        Long currentUserId = currentUser.getUserId();
        followService.followUser(currentUserId, userId);
        return "redirect:/profile/" + userId;
    }
}