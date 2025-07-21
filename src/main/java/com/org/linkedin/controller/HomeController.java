package com.org.linkedin.controller;

import com.org.linkedin.model.Post;
import com.org.linkedin.service.impl.PostServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final PostServiceImpl postService;

    public HomeController(PostServiceImpl postService) {
        this.postService = postService;
    }

    @GetMapping("/")
    public String getHomePage(Model model) {
        List<Post> posts = postService.getAllPosts();
        model.addAttribute("posts", posts);
        return "home-page";
    }


}