package com.org.linkedin.service;

import com.org.linkedin.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostService {
    Page<Post> findAll(Pageable pageable);
    public List<Post> searchByContent(String query);
}