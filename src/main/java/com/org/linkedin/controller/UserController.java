package com.org.linkedin.controller;

import com.org.linkedin.model.User;
import com.org.linkedin.repository.UserRepository;
import com.org.linkedin.service.UserService;
import com.org.linkedin.service.impl.CloudinaryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Optional;

@Controller
public class UserController {

    private final UserService userService;
    private final CloudinaryService cloudinaryService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, CloudinaryService cloudinaryService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.cloudinaryService = cloudinaryService;
        this.userRepository = userRepository;
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
        Optional<User> optionalUser = userRepository.findByEmail(email);
        User user = optionalUser.orElse(null);

        if (user == null || !user.getPassword().equals(password)) {
            model.addAttribute("error", "Invalid email or password");
            return "login";
        }

        session.setAttribute("loggedInUser", user);
        return "redirect:/";
    }

    @GetMapping("/user/profile")
    public String viewProfile(Model model, Principal principal) {
        String email = principal.getName();
        Optional<User> optionalUser = userRepository.findByEmail(email);
        model.addAttribute("user", optionalUser.orElse(null));
        return "user-profile";
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/register")
    public String signupSubmit(@ModelAttribute("user") User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "redirect:/login";
    }
}