package com.org.linkedin.controller;

import com.org.linkedin.model.Post;
import com.org.linkedin.model.User;
import com.org.linkedin.repository.PostRepository;
import com.org.linkedin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/create")
    public String showCreatePostForm(Model model) {
        model.addAttribute("post", new Post());
        model.addAttribute("authorName", "Sasikala Nalagatla");
        return "create_post";
    }

    @PostMapping("/create")
    public String createPost(@ModelAttribute("post") Post post) {
        String dummyUserName = "sasikala";
        User user = userRepository.findByFullName(dummyUserName);

        if (user != null) {
            post.setAuthorName(user.getFullName());
        } else {
            post.setAuthorName("Guest");
        }

        postRepository.save(post);
        return "redirect:/post/list";
    }

    @GetMapping("/list")
    public String listPosts(Model model) {
        model.addAttribute("posts", postRepository.findAll());
        return "post_list.html";
    }

    @GetMapping("/edit/{id}")
    public String editPostForm(@PathVariable Long id, Model model) {
        Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));
        model.addAttribute("post", post);
        model.addAttribute("authorName", post.getAuthorName());
        return "edit_post";
    }

    @PostMapping("/edit/{id}")
    public String updatePost(@PathVariable Long id, @ModelAttribute("post") Post updatedPost) {
        Post post = postRepository.findById(id).orElseThrow(() -> new RuntimeException("Post not found"));

        post.setPostDescription(updatedPost.getPostDescription());
        post.setEdited(true);

        postRepository.save(post);
        return "redirect:/post/list";
    }

    @GetMapping("/delete/{id}")
    public String deletePost(@PathVariable Long id) {
        postRepository.deleteById(id);
        return "redirect:/";
    }
}