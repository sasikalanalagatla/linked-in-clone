package com.org.linkedin.service.impl;

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
        Reaction existing = reactionRepository.findByUserAndPost(user, post);
        if (existing != null) {
            post.getReactions().remove(existing);
            reactionRepository.delete(existing);
        } else {
            Reaction reaction = new Reaction();
            reaction.setUser(user);
            reaction.setPost(post);
            reaction.setReactionType("LIKE");
            post.getReactions().add(reaction);
            reactionRepository.save(reaction);
        }
        post.setTotalReactions(post.getReactions().size());
        postRepository.save(post);
    }

}