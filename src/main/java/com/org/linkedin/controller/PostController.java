package com.org.linkedin.controller;

import com.org.linkedin.model.Post;
import com.org.linkedin.model.User;
import com.org.linkedin.repository.PostRepository;
import com.org.linkedin.repository.UserRepository;
import com.org.linkedin.service.ReactionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PostController {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ReactionService reactionService;

    public PostController(PostRepository postRepository, UserRepository userRepository,
                          ReactionService reactionService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.reactionService = reactionService;
    }

    @GetMapping("/post/create")
    public String showCreatePostForm(Model model) {
        model.addAttribute("post", new Post());
        model.addAttribute("authorName", "Sasikala Nalagatla");
        return "create_post";
    }

    @PostMapping("/post/create")
    public String createPost(@ModelAttribute("post") Post post) {
        User user = userRepository.findByFullName("sasikala");
        if (user != null) {
            post.setAuthorName(user.getFullName());
        } else {
            post.setAuthorName("Guest");
        }
        postRepository.save(post);
        return "redirect:/";
    }

    @GetMapping("/")
    public String getPostFeed(Model model) {
        List<Post> posts = postRepository.findAll();
        User dummyUser = userRepository.findByFullName("sasikala");

        Map<Long, Boolean> postUserLikes = new HashMap<>();
        for (Post post : posts) {
            post.setTotalReactions(post.getReactions().size());
            if (dummyUser != null) {
                boolean liked = reactionService.hasUserLikedPost(post, dummyUser);
                postUserLikes.put(post.getPostId(), liked);
            }
        }

        model.addAttribute("posts", posts);
        model.addAttribute("postUserLikes", postUserLikes);
        return "home-page";
    }

    @GetMapping("/post/edit/{id}")
    public String editPostForm(@PathVariable Long id, Model model) {
        Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));
        model.addAttribute("post", post);
        model.addAttribute("authorName", post.getAuthorName());
        return "edit_post";
    }

    @PostMapping("/post/edit/{id}")
    public String updatePost(@PathVariable Long id, @ModelAttribute("post") Post updatedPost) {
        Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));
        post.setPostDescription(updatedPost.getPostDescription());
        post.setEdited(true);
        postRepository.save(post);
        return "redirect:/";
    }

    @GetMapping("/post/delete/{id}")
    public String deletePost(@PathVariable Long id) {
        postRepository.deleteById(id);
        return "redirect:/";
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

}