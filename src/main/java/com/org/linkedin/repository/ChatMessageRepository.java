package com.org.linkedin.repository;

import com.org.linkedin.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findBySenderEmailAndReceiverEmailOrderByTimestamp(String senderEmail, String receiverEmail);
    List<ChatMessage> findByReceiverEmailAndSenderEmailOrderByTimestamp(String receiverEmail, String senderEmail);
}
