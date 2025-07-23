package com.org.linkedin.repository;

import com.org.linkedin.model.Message;
import com.org.linkedin.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderAndReceiverOrderByTimestamp(User sender, User receiver);
}