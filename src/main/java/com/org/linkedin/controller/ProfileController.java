package com.org.linkedin.controller;

import com.org.linkedin.model.Education;
import com.org.linkedin.model.User;
import com.org.linkedin.service.impl.EducationServiceImpl;
import com.org.linkedin.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProfileController {

    private final UserServiceImpl userService;

    private final EducationServiceImpl educationServiceImpl;

    public ProfileController(UserServiceImpl userService, EducationServiceImpl educationServiceImpl) {
        this.userService = userService;
        this.educationServiceImpl = educationServiceImpl;
    }

    @GetMapping("/profile/{userId}")
    public String showProfile(@PathVariable("userId") Long userId,
                              Model model) {
        User user = userService.getUserById(2l);
        model.addAttribute("user", user);
        String email = user.getEmail();
        model.addAttribute("email",email);
        return "user-profile";
    }

    @GetMapping("/profile/edit/{id}")
    public String showEditProfileForm(@PathVariable Long id, Model model) {
        User user = userService.getUserById(2l);
        model.addAttribute("user", user);
        return "edit-profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute("user") User updatedUser) {
        userService.updateUser(updatedUser);
        return "redirect:/profile/" + 2;
    }


}
