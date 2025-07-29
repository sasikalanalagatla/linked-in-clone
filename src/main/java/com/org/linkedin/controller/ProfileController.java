package com.org.linkedin.controller;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.User;
import com.org.linkedin.repository.UserRepository;
import com.org.linkedin.service.ConnectionRequestService;
import com.org.linkedin.service.impl.CloudinaryService;
import com.org.linkedin.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@Controller
public class ProfileController {

    private final UserServiceImpl userService;
    private final CloudinaryService cloudinaryService;
    private final ConnectionRequestService connectionRequestService;

    @Autowired
    public ProfileController(UserServiceImpl userService, CloudinaryService cloudinaryService, ConnectionRequestService connectionRequestService) {
        this.userService = userService;
        this.cloudinaryService = cloudinaryService;
        this.connectionRequestService = connectionRequestService;

    }

    @GetMapping("/profile/{userId}")
    public String showProfile(@PathVariable("userId") Long userId, Model model, Principal principal) {
        if (userId == null) {
            throw new CustomException("INVALID_USER_ID", "User ID cannot be null");
        }
        String email = principal.getName();
        User loggedInUser = userService.findByEmail(email);
        User viewedUser = userService.getUserById(userId);

        String connectionStatus = connectionRequestService.getConnectionStatus(loggedInUser, viewedUser);

        boolean alreadyFollowing = loggedInUser.getFollowing().contains(viewedUser);

        model.addAttribute("user", viewedUser);
        model.addAttribute("email", viewedUser.getEmail());
        model.addAttribute("currentUserId", loggedInUser.getUserId());
        model.addAttribute("isConnected", userId.equals(loggedInUser.getUserId()));
        model.addAttribute("connectionStatus", connectionStatus);
        model.addAttribute("alreadyFollowing", alreadyFollowing);

        return "user-profile";
    }

    @GetMapping("/profile/edit/{id}")
    public String showEditProfileForm(@PathVariable Long id, Model model) {
        if (id == null) {
            throw new CustomException("INVALID_USER_ID", "User ID cannot be null");
        }
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "edit-profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute("user") User updatedUser,
                                @RequestParam("profileImage") MultipartFile profileImage) throws IOException {
        if (updatedUser == null || updatedUser.getUserId() == null) {
            throw new CustomException("INVALID_USER", "User data or ID cannot be null");
        }
        if (profileImage != null && !profileImage.isEmpty()) {
            String imageUrl = cloudinaryService.uploadFile(profileImage);
            updatedUser.setProfilePictureUrl(imageUrl);
        }

        userService.updateUser(updatedUser);
        return "redirect:/profile/" + updatedUser.getUserId();
    }
}