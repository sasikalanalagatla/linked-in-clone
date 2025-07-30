package com.org.linkedin.controller;

import com.org.linkedin.model.Comment;
import com.org.linkedin.service.CommentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/posts/{postId}/comments")
    public String getCommentFragment(@PathVariable Long postId,
                                     @RequestParam(defaultValue = "0") int page,
                                     Model model) {

        List<Comment> comments = commentService.getComments(postId, page, 5);
        model.addAttribute("comments", comments);
        return "fragments/commentList :: commentList(comments=${comments})";
    }

    @PostMapping("/posts/{postId}/comments")
    public String addComment(@PathVariable Long postId,
                             @RequestBody Map<String, String> payload) {
        String text = payload.get("text");
        commentService.addComment(postId, text);
        return "redirect:/posts/{postId}/comments";
    }
}