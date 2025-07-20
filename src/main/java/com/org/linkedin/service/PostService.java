package com.org.linkedin.service;

import com.org.linkedin.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostService {
    Post savePost(Post post);
    List<Post> getAllPosts();
    Optional<Post> getPostById(Long id);
    Post updatePost(Long id, Post post);
    void deletePost(Long id);
}