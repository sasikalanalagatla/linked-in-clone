package com.org.linkedin.controller;

import com.org.linkedin.model.User;
import com.org.linkedin.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/profile")
    public String viewProfile(Model model) {
        User user = userService.getUserById(2l);
        model.addAttribute("user", user);
        return "user-profile";
    }

}