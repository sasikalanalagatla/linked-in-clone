package com.org.linkedin.controller;

import com.org.linkedin.model.User;
import com.org.linkedin.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProfileController {

    private final UserServiceImpl userService;

    @Autowired
    public ProfileController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("/profile/{userId}")
    public String showProfile(@PathVariable("userId") Long userId, Model model, HttpSession session) {
        User user = userService.getUserById(userId);
        if (user == null) {
            // Return a default user or redirect if no user is found
            user = new User(); // Placeholder, adjust as per your User class
            user.setUserId(userId);
            user.setFullName("Unknown User");
            user.setEmail("unknown@example.com");
        }
        model.addAttribute("user", user);
        model.addAttribute("email", user.getEmail());
        model.addAttribute("currentUserId", 1L); // Hardcoded for development
        model.addAttribute("isConnected", userId.equals(1L)); // True if viewing own profile
        return "user-profile";
    }

    @GetMapping("/profile/edit/{id}")
    public String showEditProfileForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        if (user == null) {
            user = new User(); // Placeholder
            user.setUserId(id);
            user.setFullName("Unknown User");
            user.setEmail("unknown@example.com");
        }
        model.addAttribute("user", user);
        return "edit-profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute("user") User updatedUser) {
        userService.updateUser(updatedUser);
        return "redirect:/profile/" + updatedUser.getUserId();
    }
}