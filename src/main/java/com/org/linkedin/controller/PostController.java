package com.org.linkedin.controller;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.Post;
import com.org.linkedin.model.User;
import com.org.linkedin.service.ConnectionRequestService;
import com.org.linkedin.service.UserService;
import com.org.linkedin.service.impl.PostServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Controller
public class PostController {

    private final PostServiceImpl postService;
    private final UserService userService;
    private final ConnectionRequestService connectionRequestService;

    public PostController(PostServiceImpl postService, UserService userService,
                          ConnectionRequestService connectionRequestService) {
        this.postService = postService;
        this.userService = userService;
        this.connectionRequestService = connectionRequestService;
    }

    @GetMapping("/post/create")
    public String showCreatePostForm(Model model, Principal principal) {
        try {
            if (principal == null) {
                throw new CustomException("UNAUTHORIZED", "User must be logged in");
            }
            User user = userService.findByEmail(principal.getName());
            model.addAttribute("post", new Post());
            model.addAttribute("authorName", user.getFullName());
            return "create_post";
        } catch (CustomException e) {
            model.addAttribute("error", "Error loading post form: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/post/create")
    public String createPost(@ModelAttribute("post") Post post,
                             @RequestParam("imageFile") MultipartFile imageFile,
                             Principal principal) {
        try {
            postService.createPost(post, imageFile, principal);
            return "redirect:/";
        } catch (CustomException e) {
            return "redirect:/?error=" + e.getMessage();
        }
    }

    @GetMapping("/")
    public String getPostFeed(Model model, Principal principal) {
        try {
            if (principal == null) {
                throw new CustomException("UNAUTHORIZED", "User must be logged in");
            }
            User user = userService.findByEmail(principal.getName());
            Pageable pageable = PageRequest.of(0, 10, Sort.by("postId").descending());
            Page<Post> postPage = postService.findAll(pageable);
            List<User> connections = connectionRequestService.getConnections(user);
            Integer totalConnection = connections.size();

            model.addAttribute("user", user);
            model.addAttribute("currentUserId", user.getUserId());
            model.addAttribute("totalConnection", totalConnection);
            model.addAttribute("posts", postPage.getContent());
            return "home-page";
        } catch (CustomException e) {
            model.addAttribute("error", "Error loading post feed: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/post/edit/{id}")
    public String editPostForm(@PathVariable Long id, Model model, Principal principal) {
        try {
            if (principal == null) {
                throw new CustomException("UNAUTHORIZED", "User must be logged in");
            }
            User user = userService.findByEmail(principal.getName());
            Post post = postService.getPostById(id);
            if (!post.getAuthor().getUserId().equals(user.getUserId())) {
                throw new CustomException("UNAUTHORIZED", "You can only edit your own posts");
            }
            model.addAttribute("post", post);
            model.addAttribute("authorName", post.getAuthorName());
            return "edit_post";
        } catch (CustomException e) {
            model.addAttribute("error", "Error loading edit form: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/post/edit/{id}")
    public String updatePost(@PathVariable Long id,
                             @ModelAttribute("post") Post updatedPost,
                             Model model,
                             Principal principal) {
        try {
            postService.updatePost(id, updatedPost, principal);
            User user = userService.findByEmail(principal.getName());
            return "redirect:/profile/" + user.getUserId();
        } catch (CustomException e) {
            model.addAttribute("error", "Error updating post: " + e.getMessage());
            return "edit_post";
        }
    }

    @PostMapping("/post/delete/{id}")
    public String deletePost(@PathVariable Long id, Model model, Principal principal) {
        try {
            postService.deletePost(id, principal);
            User user = userService.findByEmail(principal.getName());
            return "redirect:/profile/" + user.getUserId();
        } catch (CustomException e) {
            model.addAttribute("error", "Error deleting post: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/post/react/{postId}")
    @ResponseBody
    public String reactToPost(@PathVariable Long postId, Principal principal) {
        try {
            return postService.reactToPost(postId, principal);
        } catch (CustomException e) {
            return "Error: " + e.getMessage();
        }
    }

    @GetMapping("/post/all")
    public String viewAllPosts(Model model, Principal principal) {
        try {
            if (principal == null) {
                throw new CustomException("UNAUTHORIZED", "User must be logged in");
            }
            User user = userService.findByEmail(principal.getName());
            List<Post> posts = postService.viewAllPosts(principal);
            model.addAttribute("posts", posts);
            model.addAttribute("currentUserId", user.getUserId());
            return "post_list";
        } catch (CustomException e) {
            model.addAttribute("error", "Error loading posts: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/loadMorePosts")
    public String loadMorePosts(@RequestParam int page, Model model, Principal principal) {
        try {
            if (principal == null) {
                throw new CustomException("UNAUTHORIZED", "User must be logged in");
            }
            User user = userService.findByEmail(principal.getName());
            Pageable pageable = PageRequest.of(page, 10, Sort.by("postId").descending());
            Page<Post> postPage = postService.loadMorePosts(page, principal, pageable);
            model.addAttribute("posts", postPage.getContent());
            model.addAttribute("currentUserId", user.getUserId());
            return "partials/postCards :: postList";
        } catch (CustomException e) {
            model.addAttribute("error", "Error loading more posts: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/search")
    public String search(
            @RequestParam("query") String query,
            @RequestParam(value = "filter", defaultValue = "all") String filter,
            Model model) {
        try {
            List<User> peopleResults = filter.equals("posts") ? List.of() : userService.searchByName(query);
            List<Post> postResults = filter.equals("people") ? List.of() : postService.searchByContent(query);
            model.addAttribute("query", query);
            model.addAttribute("filter", filter);
            model.addAttribute("peopleResults", peopleResults);
            model.addAttribute("postResults", postResults);
            return "search";
        } catch (CustomException e) {
            model.addAttribute("error", "Error searching: " + e.getMessage());
            return "error";
        }
    }
}