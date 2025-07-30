package com.org.linkedin.service.impl;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.Post;
import com.org.linkedin.model.User;
import com.org.linkedin.repository.PostRepository;
import com.org.linkedin.service.ConnectionRequestService;
import com.org.linkedin.service.PostService;
import com.org.linkedin.service.ReactionService;
import com.org.linkedin.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserService userService;
    private final ReactionService reactionService;
    private final CloudinaryService cloudinaryService;

    public PostServiceImpl(PostRepository postRepository, UserService userService,
                           ReactionService reactionService, CloudinaryService cloudinaryService) {
        this.postRepository = postRepository;
        this.userService = userService;
        this.reactionService = reactionService;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    public Page<Post> findAll(Pageable pageable) {
        if (pageable == null) {
            throw new CustomException("INVALID_PAGEABLE", "Pageable cannot be null");
        }
        return postRepository.findAll(pageable);
    }

    @Override
    public List<Post> searchByContent(String query) {
        return postRepository.findByPostDescriptionContainingIgnoreCase(query);
    }

    @Override
    public Post createPost(Post post, MultipartFile imageFile, Principal principal) {
        validatePrincipal(principal);
        User user = getCurrentUser(principal);

        post.setAuthor(user);
        post.setAuthorName(user.getFullName());
        post.setTotalReactions(0);

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String imageUrl = cloudinaryService.uploadFile(imageFile);
                post.setImageUrl(imageUrl);
            } catch (IOException e) {
                throw new CustomException("IMAGE_UPLOAD_FAILED", "Failed to upload image: " + e.getMessage());
            }
        }

        return postRepository.save(post);
    }

    @Override
    public Post updatePost(Long id, Post updatedPost, Principal principal) {
        validatePrincipal(principal);
        User user = getCurrentUser(principal);

        if (updatedPost == null || updatedPost.getPostDescription() == null) {
            throw new CustomException("INVALID_POST", "Post data or description cannot be null");
        }

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new CustomException("POST_NOT_FOUND", "Post not found"));

        if (!post.getAuthor().getUserId().equals(user.getUserId())) {
            throw new CustomException("UNAUTHORIZED", "You can only edit your own posts");
        }

        post.setPostDescription(updatedPost.getPostDescription());
        post.setEdited(true);
        post.setTotalReactions(post.getReactions().size());
        return postRepository.save(post);
    }

    @Override
    public void deletePost(Long id, Principal principal) {
        validatePrincipal(principal);
        User user = getCurrentUser(principal);

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new CustomException("POST_NOT_FOUND", "Post not found"));

        if (!post.getAuthor().getUserId().equals(user.getUserId())) {
            throw new CustomException("UNAUTHORIZED", "You can only delete your own posts");
        }

        postRepository.deleteById(id);
    }

    @Override
    public Post getPostById(Long id) {
        if (id == null) {
            throw new CustomException("INVALID_POST_ID", "Post ID cannot be null");
        }
        return postRepository.findById(id)
                .orElseThrow(() -> new CustomException("POST_NOT_FOUND", "Post not found"));
    }

    @Override
    public String reactToPost(Long postId, Principal principal) {
        validatePrincipal(principal);
        User user = getCurrentUser(principal);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException("POST_NOT_FOUND", "Post not found"));

        reactionService.toggleReaction(user, post);
        post.setTotalReactions(post.getReactions().size());
        postRepository.save(post);
        return "Reaction updated";
    }

    @Override
    public List<Post> viewAllPosts(Principal principal) {
        validatePrincipal(principal);
        User user = getCurrentUser(principal);

        List<Post> posts = postRepository.findAll();
        posts.forEach(post -> post.setTotalReactions(post.getReactions().size()));
        return posts;
    }

    @Override
    public Page<Post> loadMorePosts(int page, Principal principal, Pageable pageable) {
        validatePrincipal(principal);
        getCurrentUser(principal);
        Page<Post> postPage = findAll(pageable);
        postPage.getContent().forEach(post -> post.setTotalReactions(post.getReactions().size()));
        return postPage;
    }

    private void validatePrincipal(Principal principal) {
        if (principal == null) {
            throw new CustomException("UNAUTHORIZED", "User must be logged in");
        }
    }

    private User getCurrentUser(Principal principal) {
        String email = principal.getName();
        return userService.findByEmail(email);
    }
}