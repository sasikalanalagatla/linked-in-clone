package com.org.linkedin.repository;

import com.org.linkedin.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("SELECT cm FROM ChatMessage cm WHERE " +
            "(cm.senderEmail = :user1 AND cm.receiverEmail = :user2) OR " +
            "(cm.senderEmail = :user2 AND cm.receiverEmail = :user1) " +
            "ORDER BY cm.timestamp ASC")
    List<ChatMessage> findChatHistoryBetweenUsers(@Param("user1") String user1, @Param("user2") String user2);

    List<ChatMessage> findBySenderEmailOrderByTimestampDesc(String senderEmail);

    List<ChatMessage> findByReceiverEmailOrderByTimestampDesc(String receiverEmail);
}