package com.org.linkedin.service.impl;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.Post;
import com.org.linkedin.repository.PostRepository;
import com.org.linkedin.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Override
    public Post savePost(Post post) {
        if (post == null) {
            throw new CustomException("INVALID_POST", "Post data cannot be null");
        }
        return postRepository.save(post);
    }

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Override
    public Optional<Post> getPostById(Long id) {
        if (id == null) {
            throw new CustomException("INVALID_POST_ID", "Post ID cannot be null");
        }
        return postRepository.findById(id);
    }

    @Override
    public Post updatePost(Long id, Post updatedPost) {
        if (id == null) {
            throw new CustomException("INVALID_POST_ID", "Post ID cannot be null");
        }
        if (updatedPost == null) {
            throw new CustomException("INVALID_POST", "Post data cannot be null");
        }
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new CustomException("POST_NOT_FOUND", "Post with ID " + id + " not found"));
        post.setPostDescription(updatedPost.getPostDescription());
        post.setEdited(true);
        return postRepository.save(post);
    }

    @Override
    public void deletePost(Long id) {
        if (id == null) {
            throw new CustomException("INVALID_POST_ID", "Post ID cannot be null");
        }
        if (!postRepository.existsById(id)) {
            throw new CustomException("POST_NOT_FOUND", "Post with ID " + id + " not found");
        }
        postRepository.deleteById(id);
    }

    @Override
    public Page<Post> findAll(Pageable pageable) {
        if (pageable == null) {
            throw new CustomException("INVALID_PAGEABLE", "Pageable cannot be null");
        }
        return postRepository.findAll(pageable);
    }
}