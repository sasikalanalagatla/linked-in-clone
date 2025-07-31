package com.org.linkedin.service;

import com.org.linkedin.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

public interface PostService {

    Page<Post> findAll(Pageable pageable);
    List<Post> searchByContent(String query);
    Post createPost(Post post, MultipartFile imageFile, Principal principal);
    Post updatePost(Long id, Post updatedPost, Principal principal);
    void deletePost(Long id, Principal principal);
    Post getPostById(Long id);
    String reactToPost(Long postId, Principal principal);
    List<Post> viewAllPosts(Principal principal);
    Page<Post> loadMorePosts(int page, Principal principal, Pageable pageable);
}