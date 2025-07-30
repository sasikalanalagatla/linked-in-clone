package com.org.linkedin.service.impl;

import com.org.linkedin.model.Comment;
import com.org.linkedin.model.Post;
import com.org.linkedin.model.User;
import com.org.linkedin.repository.CommentRepository;
import com.org.linkedin.repository.PostRepository;
import com.org.linkedin.repository.UserRepository;
import com.org.linkedin.service.CommentService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentServiceImpl(CommentRepository commentRepo, PostRepository postRepo, UserRepository userRepo) {
        this.commentRepository = commentRepo;
        this.postRepository = postRepo;
        this.userRepository = userRepo;
    }

    @Override
    public List<Comment> getComments(Long postId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("commentCreatedAt").descending());
        return commentRepository.findCommentsByPostId(postId, pageable).getContent();
    }

    @Override
    public Comment addComment(Long postId, String text) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User user = getCurrentUser();

        Comment comment = new Comment();
        comment.setCommentContent(text);
        comment.setUser(user);
        comment.setPost(post);
        comment.setCommentEdited(false);
        post.setCommentsCount(post.getCommentsCount()+1);
        return commentRepository.save(comment);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
