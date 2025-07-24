package com.org.linkedin.controller;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.Post;
import com.org.linkedin.model.User;
import com.org.linkedin.repository.PostRepository;
import com.org.linkedin.repository.UserRepository;
import com.org.linkedin.service.ConnectionRequestService;
import com.org.linkedin.service.PostService;
import com.org.linkedin.service.ReactionService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class PostController {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ReactionService reactionService;
    private final CloudinaryService cloudinaryService;
    private final ConnectionRequestService connectionRequestService;
    private final PostService postService;

    public PostController(PostRepository postRepository, UserRepository userRepository,
                          ReactionService reactionService, CloudinaryService cloudinaryService,
                          ConnectionRequestService connectionRequestService, PostService postService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.reactionService = reactionService;
        this.cloudinaryService = cloudinaryService;
        this.connectionRequestService = connectionRequestService;
        this.postService = postService;
    }

    @GetMapping("/post/create")
    public String showCreatePostForm(Model model, Principal principal) {
        model.addAttribute("post", new Post());
        String email = principal.getName();
        Optional<User> optionalUser = userRepository.findByEmail(email);
        User user = optionalUser.get();

        model.addAttribute("authorName", user.getFullName());
        return "create_post";
    }

    @PostMapping("/post/create")
    public String createPost(@ModelAttribute("post") Post post,
                             @RequestParam("imageFile") MultipartFile imageFile,
                             Principal principal) {
        String email = principal.getName();
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user = userOptional.get();
        post.setAuthorId(user.getUserId());
        if (user != null) {
            post.setAuthorName(user.getFullName());
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String imageUrl = cloudinaryService.uploadFile(imageFile);
                post.setImageUrl(imageUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        postRepository.save(post);
        return "redirect:/";
    }

    @GetMapping("/")
    public String getPostFeed(Model model, Principal principal) {
        String email = principal.getName();
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user = userOptional.get();

        Pageable pageable = PageRequest.of(0, 10, Sort.by("postId").descending());
        Page<Post> postPage = postService.findAll(pageable);
        List<Post> posts = postPage.getContent();

        List<User> connections = connectionRequestService.getConnections(user);
        Integer totalConnection = connections.size();

        Map<Long, Boolean> postUserLikes = new HashMap<>();
        for (Post post : posts) {
            post.setTotalReactions(post.getReactions().size());
            boolean liked = reactionService.hasUserLikedPost(post, user);
            postUserLikes.put(post.getPostId(), liked);
        }

        model.addAttribute("user", user);
        model.addAttribute("totalConnection", totalConnection);
        model.addAttribute("posts", posts);
        model.addAttribute("postUserLikes", postUserLikes);

        return "home-page";
    }

//    @GetMapping("/post/options/{id}")
//    public String showPostOptions(@PathVariable Long id, Model model) {
//        if (id == null) {
//            throw new CustomException("INVALID_POST_ID", "Post ID cannot be null");
//        }
//        Post post = postRepository.findById(id)
//                .orElseThrow(() -> new CustomException("POST_NOT_FOUND", "Post not found"));
//
//        User user = userRepository.findByFullName("sasikala");
//        Pageable pageable = PageRequest.of(0, 10, Sort.by("postId").descending());
//        Page<Post> postPage = postService.findAll(pageable);
//        List<Post> posts = postPage.getContent();
//
//        List<User> connections = connectionRequestService.getConnections(user);
//        Integer totalConnection = connections.size();
//
//        Map<Long, Boolean> postUserLikes = new HashMap<>();
//        for (Post p : posts) {
//            p.setTotalReactions(p.getReactions().size());
//            boolean liked = reactionService.hasUserLikedPost(p, user);
//            postUserLikes.put(p.getPostId(), liked);
//        }
//
//        model.addAttribute("user", user);
//        model.addAttribute("totalConnection", totalConnection);
//        model.addAttribute("posts", posts);
//        model.addAttribute("postUserLikes", postUserLikes);
//        model.addAttribute("selectedPost", id);
//
//        return "home-page";
//    }

    @GetMapping("/post/edit/{id}")
    public String editPostForm(@PathVariable Long id, Model model) {
        if (id == null) {
            throw new CustomException("INVALID_POST_ID", "Post ID cannot be null");
        }
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new CustomException("POST_NOT_FOUND", "Post not found"));
        model.addAttribute("post", post);
        model.addAttribute("authorName", post.getAuthorName());
        return "edit_post";
    }

    @PostMapping("/post/edit/{id}")
    public String updatePost(@PathVariable Long id, @ModelAttribute("post") Post updatedPost, Model model) {
        if (id == null) {
            throw new CustomException("INVALID_POST_ID", "Post ID cannot be null");
        }
        if (updatedPost == null || updatedPost.getPostDescription() == null) {
            throw new CustomException("INVALID_POST", "Post data or description cannot be null");
        }
        try {
            Post post = postRepository.findById(id)
                    .orElseThrow(() -> new CustomException("POST_NOT_FOUND", "Post not found"));
            post.setPostDescription(updatedPost.getPostDescription());
            post.setEdited(true);
            postRepository.save(post);
            return "redirect:/";
        } catch (CustomException e) {
            model.addAttribute("post", updatedPost);
            model.addAttribute("error", "Error updating post: " + e.getMessage());
            return "edit_post";
        }
    }

    @PostMapping("/post/delete/{id}")
    public String deletePost(@PathVariable Long id, Model model) {
        if (id == null) {
            throw new CustomException("INVALID_POST_ID", "Post ID cannot be null");
        }
        try {
            postRepository.findById(id)
                    .orElseThrow(() -> new CustomException("POST_NOT_FOUND", "Post not found"));
            postRepository.deleteById(id);
            return "redirect:/";
        } catch (CustomException e) {
            model.addAttribute("error", "Error deleting post: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/post/react/{postId}")
    @ResponseBody
    public String reactToPost(@PathVariable Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User dummyUser = userRepository.findByFullName("sasikala");
        if (dummyUser == null) {
            throw new RuntimeException("Dummy user 'sasikala' not found");
        }

        reactionService.toggleReaction(dummyUser, post);
        return "Reaction updated";
    }

    @GetMapping("/post/all")
    public String viewAllPosts(Model model) {
        List<Post> posts = postRepository.findAll();
        for (Post post : posts) {
            post.setTotalReactions(post.getReactions().size());
        }
        model.addAttribute("posts", posts);
        return "post_list";
    }

    @GetMapping("/loadMorePosts")
    public String loadMorePosts(@RequestParam int page, Model model) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("postId").descending());
        Page<Post> postPage = postService.findAll(pageable);
        model.addAttribute("posts", postPage.getContent());
        return "partials/postCards :: postList";
    }
}