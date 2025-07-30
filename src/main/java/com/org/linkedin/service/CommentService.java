package com.org.linkedin.service;

import com.org.linkedin.model.Comment;

import java.util.List;

public interface CommentService {

    List<Comment> getComments(Long postId, int page, int size);
    Comment addComment(Long postId, String text);
}
