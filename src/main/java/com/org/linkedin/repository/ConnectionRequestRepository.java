package com.org.linkedin.repository;

import com.org.linkedin.model.ConnectionRequest;
import com.org.linkedin.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConnectionRequestRepository extends JpaRepository<ConnectionRequest, Long> {

    List<ConnectionRequest> findByReceiverAndStatus(User receiver, String status);

    @Query("SELECT cr FROM ConnectionRequest cr WHERE " +
            "((cr.sender = :user1 AND cr.receiver = :user2) OR " +
            "(cr.sender = :user2 AND cr.receiver = :user1))")
    Optional<ConnectionRequest> findConnectionBetweenUsers(@Param("user1") User user1, @Param("user2") User user2);

    @Query("SELECT cr FROM ConnectionRequest cr WHERE ((cr.sender = :user OR cr.receiver = :user) AND cr.status = " +
            "'ACCEPTED')")
    List<ConnectionRequest> findAcceptedConnectionsForUser(@Param("user") User user);

    @Query("SELECT cr FROM ConnectionRequest cr WHERE ((cr.sender = :user1 AND cr.receiver = :user2) OR (cr.sender = " +
            ":user2 AND cr.receiver = :user1)) AND cr.status = 'PENDING'")
    Optional<ConnectionRequest> findPendingConnectionBetweenUsers(@Param("user1") User user1,
                                                                  @Param("user2") User user2);

    @Query("SELECT cr FROM ConnectionRequest cr WHERE ((cr.sender = :user1 AND cr.receiver = :user2) OR " +
            "(cr.sender = :user2 AND cr.receiver = :user1)) AND cr.status = 'ACCEPTED'")
    Optional<ConnectionRequest> findAcceptedConnectionBetweenUsers(@Param("user1") User user1,
                                                                   @Param("user2") User user2);

}