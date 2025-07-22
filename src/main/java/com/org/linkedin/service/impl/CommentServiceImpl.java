package com.org.linkedin.service.impl;

import com.org.linkedin.model.Comment;
import com.org.linkedin.model.Post;
import com.org.linkedin.model.User;
import com.org.linkedin.repository.CommentRepository;
import com.org.linkedin.service.CommentService;
import com.org.linkedin.service.PostService;
import com.org.linkedin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;
    private final PostService postService;

    @Override
    public Comment createComment(String content, Long userId, Long postId, Long parentCommentId) {
        try {
            User user = userService.getUserById(userId);
            Optional<Post> post = postService.getPostById(postId);

            Comment comment = new Comment();
            comment.setCommentContent(content);
            comment.setUser(user);
            comment.setPost(post.get());
            comment.setCommentEdited(false);

            if (parentCommentId != null) {
                Comment parentComment = getCommentById(parentCommentId);
                parentComment.addReply(comment);
                commentRepository.save(parentComment); // Save parent to cascade the operation
            }

            return commentRepository.save(comment);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create comment: " + e.getMessage());
        }
    }

    @Override
    public List<Comment> getCommentsByPost(Long postId) {
        return commentRepository.findByPostPostIdAndParentCommentIsNull(postId);
    }

    @Override
    public List<Comment> getRepliesByComment(Long commentId) {
        return commentRepository.findByParentCommentCommentId(commentId);
    }

    @Override
    public Comment updateComment(Long commentId, String newContent) {
        try {
            Comment comment = getCommentById(commentId);
            comment.setCommentContent(newContent);
            comment.setCommentEdited(true);
            comment.setCommentUpdatedAt(LocalDateTime.now());
            return commentRepository.save(comment);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update comment: " + e.getMessage());
        }
    }

    @Override
    public void deleteComment(Long commentId) {
        try {
            Comment comment = getCommentById(commentId);

            // First delete all replies
            if (!comment.getReplies().isEmpty()) {
                commentRepository.deleteAll(comment.getReplies());
            }

            commentRepository.delete(comment);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete comment: " + e.getMessage());
        }
    }

    @Override
    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));
    }
}