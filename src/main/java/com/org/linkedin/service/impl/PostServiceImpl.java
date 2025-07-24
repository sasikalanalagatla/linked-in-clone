package com.org.linkedin.service.impl;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.Post;
import com.org.linkedin.repository.PostRepository;
import com.org.linkedin.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public Page<Post> findAll(Pageable pageable) {
        if (pageable == null) {
            throw new CustomException("INVALID_PAGEABLE", "Pageable cannot be null");
        }
        return postRepository.findAll(pageable);
    }
}