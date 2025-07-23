package com.org.linkedin.repository;

import com.org.linkedin.model.ConnectionRequest;
import com.org.linkedin.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConnectionRequestRepository extends JpaRepository<ConnectionRequest, Long> {
    List<ConnectionRequest> findByReceiverAndStatus(User receiver, String status);
    List<ConnectionRequest> findBySenderOrReceiverAndStatus(User sender, User receiver, String status);
}