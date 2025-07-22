package com.org.linkedin.service;

import com.org.linkedin.model.ConnectionRequest;
import com.org.linkedin.model.User;

import java.util.List;

public interface ConnectionRequestService {
    List<ConnectionRequest> getPendingRequests(User user);
    void sendRequest(User sender, User receiver);
    void acceptRequest(Long requestId);
    List<User> getConnections(User user);

}
