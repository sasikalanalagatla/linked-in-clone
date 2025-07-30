package com.org.linkedin.controller;

import com.org.linkedin.model.User;
import com.org.linkedin.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@Controller
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        return "login";
    }

    @PostMapping("/login")
    public String loginSubmit(@RequestParam("email") String email,
                              @RequestParam("password") String password,
                              Model model,
                              HttpSession session) {
        try {
            User user = userService.findByEmail(email);
            if (!passwordEncoder.matches(password, user.getPassword())) {
                model.addAttribute("error", "Invalid email or password");
                return "login";
            }
            session.setAttribute("loggedInUser", user);
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", "Invalid email or password");
            return "login";
        }
    }

    @GetMapping("/user/profile")
    public String viewProfile(Model model, Principal principal) {
        try {
            String email = principal.getName();
            User user = userService.findByEmail(email);
            model.addAttribute("user", user);
            return "user-profile";
        } catch (Exception e) {
            model.addAttribute("user", null);
            return "user-profile";
        }
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/register")
    public String signupSubmit(@ModelAttribute("user") User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.updateUser(user);
        return "redirect:/login";
    }
}