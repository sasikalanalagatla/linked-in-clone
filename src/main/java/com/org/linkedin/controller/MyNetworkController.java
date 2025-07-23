package com.org.linkedin.controller;

import com.org.linkedin.model.ConnectionRequest;
import com.org.linkedin.model.User;
import com.org.linkedin.service.ConnectionRequestService;
import com.org.linkedin.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

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
        User currentUser = userService.getUserById(1L); // TODO: Replace with authentication
        List<ConnectionRequest> pendingRequests = connectionRequestService.getPendingRequests(currentUser);
        List<User> connections = connectionRequestService.getConnections(currentUser);
        List<User> followers = userService.getFollowers(currentUser);
        List<User> following = userService.getFollowing(currentUser);

        model.addAttribute("requests", pendingRequests != null ? pendingRequests : new ArrayList<>());
        model.addAttribute("connections", connections != null ? connections : new ArrayList<>());
        model.addAttribute("followers", followers != null ? followers : new ArrayList<>());
        model.addAttribute("following", following != null ? following : new ArrayList<>());
        model.addAttribute("currentUserId", currentUser != null ? currentUser.getUserId() : 1L);

        return "mynetwork";
    }

    @GetMapping("/profile/{id}/followers")
    public String showFollowers(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        List<User> followers = userService.getFollowers(user);
        model.addAttribute("followers", followers != null ? followers : new ArrayList<>());
        model.addAttribute("currentUserId", id);
        return "followers";
    }

    @GetMapping("/profile/{id}/following")
    public String showFollowing(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        List<User> following = userService.getFollowing(user);
        model.addAttribute("following", following != null ? following : new ArrayList<>());
        model.addAttribute("currentUserId", id);
        return "following";
    }

    @PostMapping("/accept/{requestId}")
    public String acceptConnection(@PathVariable Long requestId) {
        connectionRequestService.acceptRequest(requestId);
        return "redirect:/mynetwork";
    }

    @PostMapping("/connect/{receiverId}")
    public String sendConnectionRequest(@PathVariable Long receiverId) {
        User sender = userService.getUserById(1L); // TODO: Replace with authentication
        User receiver = userService.getUserById(receiverId);
        connectionRequestService.sendRequest(sender, receiver);
        return "redirect:/profile/" + receiverId;
    }
}