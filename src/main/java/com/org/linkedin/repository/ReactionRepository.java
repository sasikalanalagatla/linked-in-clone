package com.org.linkedin.repository;

import com.org.linkedin.model.Post;
import com.org.linkedin.model.Reaction;
import com.org.linkedin.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    Reaction findByUserAndPost(User user, Post post);
}