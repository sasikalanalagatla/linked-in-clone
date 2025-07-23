package com.org.linkedin;

import com.org.linkedin.model.Post;
import com.org.linkedin.repository.PostRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final PostRepository postRepository;

    public DataLoader(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (postRepository.count() <10) {
            for (int i = 1; i <= 1000; i++) {
                Post post = new Post();
                post.setPostDescription("Sample post " + i);
                post.setImageUrl("https://example.com/image" + i + ".jpg");
                post.setAuthorProfileImage("https://example.com/profile" + i + ".jpg");
                post.setCommentsCount(i % 10);
                post.setSharesCount(i % 5);
                post.setEdited(false);
                post.setAuthorId((long) i);
                post.setAuthorName("User " + i);
                post.setTotalReactions(i % 20);

                postRepository.save(post);
            }

            System.out.println("✅ 1000 dummy posts inserted!");
        } else {
            System.out.println("ℹ️ Posts already exist. Skipping insert.");
        }
    }
}
