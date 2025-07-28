package com.org.linkedin.controller;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.Post;
import com.org.linkedin.model.User;
import com.org.linkedin.repository.PostRepository;
import com.org.linkedin.repository.UserRepository;
import com.org.linkedin.service.ConnectionRequestService;
import com.org.linkedin.service.PostService;
import com.org.linkedin.service.ReactionService;
import com.org.linkedin.service.UserService;
import com.org.linkedin.service.impl.CloudinaryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.*;

@Controller
public class PostController {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ReactionService reactionService;
    private final CloudinaryService cloudinaryService;
    private final ConnectionRequestService connectionRequestService;
    private final PostService postService;
    private final UserService userService;

    public PostController(PostRepository postRepository, UserRepository userRepository, ReactionService reactionService, CloudinaryService cloudinaryService, ConnectionRequestService connectionRequestService, PostService postService, UserService userService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.reactionService = reactionService;
        this.cloudinaryService = cloudinaryService;
        this.connectionRequestService = connectionRequestService;
        this.postService = postService;
        this.userService = userService;
    }

    @GetMapping("/post/create")
    public String showCreatePostForm(Model model, Principal principal) {
        validatePrincipal(principal);
        User user = getCurrentUser(principal);

        model.addAttribute("post", new Post());
        model.addAttribute("authorName", user.getFullName());
        return "create_post";
    }

    @PostMapping("/post/create")
    public String createPost(@ModelAttribute("post") Post post, @RequestParam("mediaFile") MultipartFile mediaFile, Principal principal) {
        validatePrincipal(principal);
        User user = getCurrentUser(principal);

        post.setAuthor(user);
        post.setAuthorName(user.getFullName());
        post.setTotalReactions(0);

        if(mediaFile != null && !mediaFile.isEmpty()) {
            try {
                String imageUrl = cloudinaryService.uploadMedia(mediaFile);
                post.setMediaUrl(imageUrl);
            } catch(IOException e) {
                throw new CustomException("IMAGE_UPLOAD_FAILED", "Failed to upload image: " + e.getMessage());
            }
        }

        postRepository.save(post);
        return "redirect:/";
    }

    @GetMapping("/")
    public String getPostFeed(Model model, Principal principal) {
        validatePrincipal(principal);
        User user = getCurrentUser(principal);

        Pageable pageable = PageRequest.of(0, 10, Sort.by("postId").descending());
        Page<Post> postPage = postService.findAll(pageable);
        List<Post> posts = postPage.getContent();

        List<User> connections = connectionRequestService.getConnections(user);
        Integer totalConnection = connections.size();

        model.addAttribute("user", user);
        model.addAttribute("currentUserId", user.getUserId());
        model.addAttribute("totalConnection", totalConnection);
        model.addAttribute("posts", posts);

        return "home-page";
    }

    @GetMapping("/post/edit/{id}")
    public String editPostForm(@PathVariable Long id, Model model, Principal principal) {
        validatePrincipal(principal);
        User user = getCurrentUser(principal);

        Post post = postRepository.findById(id).orElseThrow(() -> new CustomException("POST_NOT_FOUND", "Post not found"));

        if(!post.getAuthor().getUserId().equals(user.getUserId())) {
            throw new CustomException("UNAUTHORIZED", "You can only edit your own posts");
        }

        model.addAttribute("post", post);
        model.addAttribute("authorName", post.getAuthorName());
        return "edit_post";
    }

    @PostMapping("/post/edit/{id}")
    public String updatePost(@PathVariable Long id, @ModelAttribute("post") Post updatedPost, Model model, Principal principal) {
        validatePrincipal(principal);
        User user = getCurrentUser(principal);

        if(updatedPost == null || updatedPost.getPostDescription() == null) {
            throw new CustomException("INVALID_POST", "Post data or description cannot be null");
        }

        Post post = postRepository.findById(id).orElseThrow(() -> new CustomException("POST_NOT_FOUND", "Post not found"));

        if(!post.getAuthor().getUserId().equals(user.getUserId())) {
            throw new CustomException("UNAUTHORIZED", "You can only edit your own posts");
        }

        post.setPostDescription(updatedPost.getPostDescription());
        post.setEdited(true);
        post.setTotalReactions(post.getReactions().size());
        postRepository.save(post);

        return "redirect:/profile/" + user.getUserId();
    }

    @PostMapping("/post/delete/{id}")
    public String deletePost(@PathVariable Long id, Model model, Principal principal) {
        validatePrincipal(principal);
        User user = getCurrentUser(principal);

        Post post = postRepository.findById(id).orElseThrow(() -> new CustomException("POST_NOT_FOUND", "Post not found"));

        if(!post.getAuthor().getUserId().equals(user.getUserId())) {
            throw new CustomException("UNAUTHORIZED", "You can only delete your own posts");
        }

        postRepository.deleteById(id);
        return "redirect:/profile/" + user.getUserId();
    }

    @PostMapping("/post/react/{postId}")
    @ResponseBody
    public String reactToPost(@PathVariable Long postId, Principal principal) {
        if(principal == null) {
            throw new CustomException("UNAUTHORIZED", "User must be logged in");
        }

        Post post = postRepository.findById(postId).orElseThrow(() -> new CustomException("POST_NOT_FOUND", "Post not found"));

        String email = principal.getName();
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()) {
            throw new CustomException("USER_NOT_FOUND", "User not found");
        }
        User user = userOptional.get();

        reactionService.toggleReaction(user, post);
        post.setTotalReactions(post.getReactions().size());
        postRepository.save(post);
        return "Reaction updated";
    }

    @GetMapping("/post/all")
    public String viewAllPosts(Model model, Principal principal) {
        validatePrincipal(principal);
        User user = getCurrentUser(principal);

        List<Post> posts = postRepository.findAll();
        posts.forEach(post -> post.setTotalReactions(post.getReactions().size()));

        model.addAttribute("posts", posts);
        model.addAttribute("currentUserId", user.getUserId());
        return "post_list";
    }

    @GetMapping("/loadMorePosts")
    public String loadMorePosts(@RequestParam int page, Model model, Principal principal) {
        validatePrincipal(principal);
        User user = getCurrentUser(principal);

        Pageable pageable = PageRequest.of(page, 10, Sort.by("postId").descending());
        Page<Post> postPage = postService.findAll(pageable);
        List<Post> posts = postPage.getContent();

        posts.forEach(post -> post.setTotalReactions(post.getReactions().size()));

        model.addAttribute("posts", posts);
        model.addAttribute("currentUserId", user.getUserId());
        return "partials/postCards :: postList";
    }

    private void validatePrincipal(Principal principal) {
        if(principal == null) {
            throw new CustomException("UNAUTHORIZED", "User must be logged in");
        }
    }

    private User getCurrentUser(Principal principal) {
        String email = principal.getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new CustomException("USER_NOT_FOUND", "User not found"));
    }

    @GetMapping("/search")
    public String search(@RequestParam("query") String query, @RequestParam(value = "filter", defaultValue = "all") String filter, Model model) {
        List<User> peopleResults = filter.equals("posts") ? List.of() : userService.searchByName(query);
        List<Post> postResults = filter.equals("people") ? List.of() : postService.searchByContent(query);

        model.addAttribute("query", query);
        model.addAttribute("filter", filter);
        model.addAttribute("peopleResults", peopleResults);
        model.addAttribute("postResults", postResults);

        return "search";
    }

}