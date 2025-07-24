package com.org.linkedin.service.impl;

import com.org.linkedin.model.Comment;
import com.org.linkedin.model.Post;
import com.org.linkedin.model.User;
import com.org.linkedin.repository.CommentRepository;
import com.org.linkedin.repository.PostRepository;
import com.org.linkedin.repository.UserRepository;
import com.org.linkedin.service.CommentService;
import com.org.linkedin.service.PostService;
import com.org.linkedin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepo;
    private final PostRepository postRepo;
    private final UserRepository userRepo;

    public CommentServiceImpl(CommentRepository commentRepo, PostRepository postRepo, UserRepository userRepo) {
        this.commentRepo = commentRepo;
        this.postRepo = postRepo;
        this.userRepo = userRepo;
    }

    @Override
    public List<Comment> getComments(Long postId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("commentCreatedAt").descending());
        return commentRepo.findCommentsByPostId(postId, pageable).getContent();
    }

    @Override
    public boolean hasMore(Long postId, int page, int size) {
        long total = commentRepo.countCommentsByPostId(postId);
        return (page + 1) * size < total;
    }

    @Override
    public Comment addComment(Long postId, String text) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        User user = getCurrentUser();

        Comment comment = new Comment();
        comment.setCommentContent(text);
        comment.setUser(user);
        comment.setPost(post);
        comment.setCommentEdited(false);

        return commentRepo.save(comment);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
