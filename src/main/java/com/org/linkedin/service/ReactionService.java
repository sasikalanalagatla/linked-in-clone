package com.org.linkedin.service;

import com.org.linkedin.model.Post;
import com.org.linkedin.model.User;

public interface ReactionService {

    void toggleReaction(User user, Post post);
}