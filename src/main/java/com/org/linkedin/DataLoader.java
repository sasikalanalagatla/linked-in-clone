/*
package com.org.linkedin;

import com.org.linkedin.model.Post;
import com.org.linkedin.model.User;
import com.org.linkedin.repository.PostRepository;
import com.org.linkedin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataLoader implements CommandLineRunner {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Value("${dataloader.post.threshold:10}")
    private int postThreshold;

    @Value("${dataloader.post.count:1000}")
    private int postCount;

    public DataLoader(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (postRepository.count() >= postThreshold) {
            System.out.println("ℹ️ Posts already exist (count >= " + postThreshold + "). Skipping insert.");
            return;
        }

        // Create or retrieve a default user for posts
        User defaultUser = userRepository.findByEmail("sasi@example.com")
                .orElseGet(() -> {
                    User user = new User();
                    user.setEmail("sasi@example.com");
                    user.setPassword("hashedPassword"); // Replace with proper password hashing
                    user.setFullName("Default User");
                    return userRepository.save(user);
                });

        for (int i = 1; i <= postCount; i++) {
            Post post = new Post();
            post.setPostDescription("Sample post " + i);
            post.setImageUrl("https://example.com/image" + i + ".jpg");
            post.setAuthorProfileImage("https://example.com/profile" + i + ".jpg");
            post.setCommentsCount(i % 10);
            post.setSharesCount(i % 5);
            post.setEdited(false);
            post.setAuthor(defaultUser); // Set the User entity
            // post.setAuthorName(defaultUser.getFullName()); // Optional, but redundant
            post.setTotalReactions(i % 20);

            postRepository.save(post);
        }

        System.out.println("✅ " + postCount + " dummy posts inserted!");
    }
}
*/
