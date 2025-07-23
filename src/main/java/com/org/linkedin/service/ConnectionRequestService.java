package com.org.linkedin.service;

import com.org.linkedin.model.ConnectionRequest;
import com.org.linkedin.model.User;

import java.util.List;
import java.util.Optional;

public interface ConnectionRequestService {
    void sendRequest(User sender, User receiver);
    void acceptRequest(Long requestId);
    void ignoreRequest(Long requestId);
    List<ConnectionRequest> getPendingRequests(User user);
    List<User> getConnections(User user);
    Optional<ConnectionRequest> getRequestById(Long requestId);
}