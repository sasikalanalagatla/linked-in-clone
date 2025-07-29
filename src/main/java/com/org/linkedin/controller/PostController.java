package com.org.linkedin.controller;

import com.org.linkedin.model.Post;
import com.org.linkedin.service.PostService;
import com.org.linkedin.service.impl.PostServiceImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@Controller
public class PostController {

    private final PostServiceImpl postServiceImpl;

    public PostController(PostServiceImpl postServiceImpl) {
        this.postServiceImpl = postServiceImpl;
    }

    @GetMapping("/post/create")
    public String showCreatePostForm(Model model, Principal principal) {
        return postServiceImpl.showCreatePostForm(model, principal);
    }

    @PostMapping("/post/create")
    public String createPost(@ModelAttribute("post") Post post,
                             @RequestParam("imageFile") MultipartFile imageFile,
                             Principal principal) {
        return postServiceImpl.createPost(post, imageFile, principal);
    }

    @GetMapping("/")
    public String getPostFeed(Model model, Principal principal) {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("postId").descending());
        return postServiceImpl.getPostFeed(model, principal, pageable);
    }

    @GetMapping("/post/edit/{id}")
    public String editPostForm(@PathVariable Long id, Model model, Principal principal) {
        return postServiceImpl.editPostForm(id, model, principal);
    }

    @PostMapping("/post/edit/{id}")
    public String updatePost(@PathVariable Long id,
                             @ModelAttribute("post") Post updatedPost,
                             Model model,
                             Principal principal) {
        return postServiceImpl.updatePost(id, updatedPost, model, principal);
    }

    @PostMapping("/post/delete/{id}")
    public String deletePost(@PathVariable Long id, Model model, Principal principal) {
        return postServiceImpl.deletePost(id, model, principal);
    }

    @PostMapping("/post/react/{postId}")
    @ResponseBody
    public String reactToPost(@PathVariable Long postId, Principal principal) {
        return postServiceImpl.reactToPost(postId, principal);
    }

    @GetMapping("/post/all")
    public String viewAllPosts(Model model, Principal principal) {
        return postServiceImpl.viewAllPosts(model, principal);
    }

    @GetMapping("/loadMorePosts")
    public String loadMorePosts(@RequestParam int page, Model model, Principal principal) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("postId").descending());
        return postServiceImpl.loadMorePosts(page, model, principal, pageable);
    }

    @GetMapping("/search")
    public String search(
            @RequestParam("query") String query,
            @RequestParam(value = "filter", defaultValue = "all") String filter,
            Model model
    ) {
        return postServiceImpl.search(query, filter, model);
    }
}