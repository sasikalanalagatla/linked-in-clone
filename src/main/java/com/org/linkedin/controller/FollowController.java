package com.org.linkedin.controller;

import com.org.linkedin.service.FollowService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/profile")
public class FollowController {

    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping("/{id}/follow")
    public String followUser(@PathVariable Long id) {
        Long currentUserId = 2L;
        followService.followUser(currentUserId, id);
        return "redirect:/profile/" + id;
    }

    @GetMapping("/{id}/followers")
    public String getFollowers(@PathVariable("id") Long id, Model model) {
        model.addAttribute("followers", followService.getFollowers(id));
        return "followers";
    }

    @GetMapping("/{id}/following")
    public String getFollowing(@PathVariable("id") Long id, Model model) {
        model.addAttribute("following", followService.getFollowing(id));
        return "following";
    }
}
