package com.org.linkedin.model;

import jakarta.persistence.Id;

import java.time.LocalDateTime;

public class Comment {

    @Id
    private Long commentId;

    private String commentContent;

    private String userName; // put string as user

    private LocalDateTime commentCreatedAt;

    private LocalDateTime commentUpdatedAt;

    private boolean isCommentEdited;
}