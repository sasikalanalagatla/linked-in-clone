package com.org.linkedin.controller;

import com.org.linkedin.model.Comment;
import com.org.linkedin.service.CommentService;
import com.org.linkedin.service.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class CommentController {

    private final CommentService commentService;
    private final PostService postService;

    public CommentController(CommentService commentService, PostService postService) {
        this.commentService = commentService;
        this.postService = postService;
    }

    @GetMapping("/posts/{postId}/comments")
    public String getCommentFragment(@PathVariable Long postId,
                                     @RequestParam(defaultValue = "0") int page,
                                     Model model) {
        System.out.println("Fetching comments for postId = " + postId);

        List<Comment> comments = commentService.getComments(postId, page, 5);
        for(Comment comment : comments){
            System.out.println(comment.getCommentId() + " " + comment.getCommentContent());
        }
        model.addAttribute("comments", comments);
        return "fragments/commentList :: commentList(comments=${comments})";
    }

    @PostMapping("/posts/{postId}/comments")
    public String addComment(@PathVariable Long postId,
                             @RequestBody Map<String, String> payload) {
        String text = payload.get("text");
        Comment comment = commentService.addComment(postId, text);
        return "redirect:/posts/{postId}/comments";
    }
}