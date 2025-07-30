package com.org.linkedin.controller;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.User;
import com.org.linkedin.service.ConnectionRequestService;
import com.org.linkedin.service.FollowService;
import com.org.linkedin.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class ConnectionRequestController {

    private final ConnectionRequestService connectionRequestService;
    private final UserService userService;
    private final FollowService followService;

    public ConnectionRequestController(ConnectionRequestService connectionRequestService, UserService userService,
                                       FollowService followService) {
        this.connectionRequestService = connectionRequestService;
        this.userService = userService;
        this.followService = followService;
    }

    @GetMapping("/mynetwork")
    public String showMyNetwork(Model model, Principal principal) {
        try {
            User currentUser = userService.findByEmail(principal.getName());
            model.addAllAttributes(connectionRequestService.getNetworkDetails(currentUser));
            return "mynetwork";
        } catch (CustomException e) {
            model.addAttribute("error", "Error loading network: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/profile/{id}/followers")
    public String showFollowers(@PathVariable Long id, Model model) {
        try {
            model.addAllAttributes(connectionRequestService.getFollowersDetails(id));
            return "followers";
        } catch (CustomException e) {
            model.addAttribute("error", "Error loading followers: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/profile/{id}/following")
    public String showFollowing(@PathVariable Long id, Model model) {
        try {
            model.addAllAttributes(connectionRequestService.getFollowingDetails(id));
            return "following";
        } catch (CustomException e) {
            model.addAttribute("error", "Error loading following: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/accept/{requestId}")
    public String acceptConnection(@PathVariable Long requestId, RedirectAttributes redirectAttributes) {
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
    public String sendConnectionRequest(@PathVariable Long receiverId, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            User currentUser = userService.findByEmail(principal.getName());
            connectionRequestService.sendConnectionRequest(currentUser.getUserId(), receiverId);
            redirectAttributes.addFlashAttribute("success", "Connection request sent successfully!");
            return "redirect:/profile/" + receiverId;
        } catch (CustomException e) {
            redirectAttributes.addFlashAttribute("error", "Error sending connection request: " + e.getMessage());
            return "redirect:/profile/" + receiverId;
        }
    }

    @PostMapping("/follow/{userId}")
    public String followUser(@PathVariable Long userId, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            User currentUser = userService.findByEmail(principal.getName());
            followService.followUser(currentUser.getUserId(), userId);
            redirectAttributes.addFlashAttribute("success", "Successfully followed user!");
            return "redirect:/profile/" + userId;
        } catch (CustomException e) {
            redirectAttributes.addFlashAttribute("error", "Error following user: " + e.getMessage());
            return "redirect:/profile/" + userId;
        }
    }
}