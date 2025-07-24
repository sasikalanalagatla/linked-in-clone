package com.org.linkedin.repository;

import com.org.linkedin.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findBySenderEmailAndReceiverEmailOrderByTimestamp(String senderEmail, String receiverEmail);
    List<ChatMessage> findByReceiverEmailAndSenderEmailOrderByTimestamp(String receiverEmail, String senderEmail);
}