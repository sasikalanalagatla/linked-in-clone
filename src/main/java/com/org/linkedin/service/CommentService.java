package com.org.linkedin.service;

import com.org.linkedin.model.Comment;
import java.util.List;

public interface CommentService {
    Comment createComment(String content, Long userId, Long postId, Long parentCommentId);
    List<Comment> getCommentsByPost(Long postId);
    List<Comment> getRepliesByComment(Long commentId);
    Comment updateComment(Long commentId, String newContent);
    void deleteComment(Long commentId);
    Comment getCommentById(Long commentId);
}