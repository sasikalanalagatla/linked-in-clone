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
            "((cr.sender = :requestSender AND cr.receiver = :requestReceiver) OR " +
            "(cr.sender = :requestReceiver AND cr.receiver = :requestSender))")
    Optional<ConnectionRequest> findConnectionBetweenUsers(@Param("requestSender") User requestSender,
                                                           @Param("requestReceiver") User requestReceiver);

    @Query("SELECT cr FROM ConnectionRequest cr WHERE ((cr.sender = :user OR cr.receiver = :user) AND cr.status = 'ACCEPTED')")
    List<ConnectionRequest> findAcceptedConnectionsForUser(@Param("user") User user);

    @Query("SELECT cr FROM ConnectionRequest cr WHERE ((cr.sender = :requestSender AND cr.receiver = :requestReceiver) OR " +
            "(cr.sender = :requestReceiver AND cr.receiver = :requestSender)) AND cr.status = 'PENDING'")
    Optional<ConnectionRequest> findPendingConnectionBetweenUsers(@Param("requestSender") User requestSender,
                                                                  @Param("requestReceiver") User requestReceiver);

    @Query("SELECT cr FROM ConnectionRequest cr WHERE ((cr.sender = :requestSender AND cr.receiver = :requestReceiver) OR " +
            "(cr.sender = :requestReceiver AND cr.receiver = :requestSender)) AND cr.status = 'ACCEPTED'")
    Optional<ConnectionRequest> findAcceptedConnectionBetweenUsers(@Param("requestSender") User requestSender,
                                                                   @Param("requestReceiver") User requestReceiver);

}