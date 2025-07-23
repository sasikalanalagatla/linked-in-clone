package com.org.linkedin.service.impl;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.Post;
import com.org.linkedin.model.Reaction;
import com.org.linkedin.model.User;
import com.org.linkedin.repository.PostRepository;
import com.org.linkedin.repository.ReactionRepository;
import com.org.linkedin.service.ReactionService;
import org.springframework.stereotype.Service;

@Service
public class ReactionServiceImpl implements ReactionService {

    private final ReactionRepository reactionRepository;
    private final PostRepository postRepository;

    public ReactionServiceImpl(ReactionRepository reactionRepository, PostRepository postRepository) {
        this.reactionRepository = reactionRepository;
        this.postRepository = postRepository;
    }

    @Override
    public void toggleReaction(User user, Post post) {
        if (user == null) {
            throw new CustomException("INVALID_USER", "User cannot be null");
        }
        if (post == null) {
            throw new CustomException("INVALID_POST", "Post cannot be null");
        }
        Reaction existing = reactionRepository.findByUserAndPost(user, post);
        if (existing != null) {
            reactionRepository.delete(existing);
            post.setTotalReactions(post.getTotalReactions() - 1);
        } else {
            Reaction reaction = new Reaction();
            reaction.setUser(user);
            reaction.setPost(post);
            reaction.setReactionType("LIKE");
            reactionRepository.save(reaction);
            post.setTotalReactions(post.getTotalReactions() + 1);
        }
        postRepository.save(post);
    }

    @Override
    public boolean hasUserLikedPost(Post post, User user) {
        if (user == null) {
            throw new CustomException("INVALID_USER", "User cannot be null");
        }
        if (post == null) {
            throw new CustomException("INVALID_POST", "Post cannot be null");
        }
        return reactionRepository.existsByUserAndPost(user, post);
    }
}