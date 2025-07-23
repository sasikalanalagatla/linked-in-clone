package com.org.linkedin.controller;

import com.org.linkedin.model.ConnectionRequest;
import com.org.linkedin.model.User;
import com.org.linkedin.service.ConnectionRequestService;
import com.org.linkedin.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
    public String showConnectionRequests(HttpSession session, Model model) {
        User currentUser = userService.getUserById(1l);

        List<ConnectionRequest> pendingRequests = connectionRequestService.getPendingRequests(currentUser);
        model.addAttribute("requests", pendingRequests);
        return "mynetwork";
    }

    @PostMapping("/accept/{requestId}")
    public String acceptConnection(@PathVariable Long requestId) {
        connectionRequestService.acceptRequest(requestId);
        return "redirect:/mynetwork";
    }

    @PostMapping("/connect/{receiverId}")
    public String sendConnectionRequest(@PathVariable Long receiverId) {
        User sender = userService.getUserById(2L);
        User receiver = userService.getUserById(receiverId);
        connectionRequestService.sendRequest(sender, receiver);
        return "redirect:/profile/" + receiverId;
    }

    @GetMapping("/connections")
    public String showAllConnections(HttpSession session, Model model) {
        User currentUser = userService.getUserById(2L);
        List<User> connections = connectionRequestService.getConnections(currentUser);
        model.addAttribute("connections", connections);
        return "connections";
    }

}
