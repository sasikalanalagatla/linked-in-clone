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
            "(cm.senderEmail = :senderEmail AND cm.receiverEmail = :receiverEmail) OR " +
            "(cm.senderEmail = :receiverEmail AND cm.receiverEmail = :senderEmail) " +
            "ORDER BY cm.timestamp ASC")
    List<ChatMessage> findChatHistoryBetweenUsers(@Param("senderEmail") String senderEmail,
                                                  @Param("receiverEmail") String receiverEmail);

    List<ChatMessage> findBySenderEmailOrderByTimestampDesc(String senderEmail);

    List<ChatMessage> findByReceiverEmailOrderByTimestampDesc(String receiverEmail);
}
