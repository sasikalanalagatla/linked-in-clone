package com.org.linkedin.service;

import com.org.linkedin.model.ConnectionRequest;
import com.org.linkedin.model.User;

import java.util.List;
import java.util.Map;

public interface ConnectionRequestService {

    void sendRequest(User sender, User receiver);
    void acceptRequest(Long requestId);
    void ignoreRequest(Long requestId);
    List<ConnectionRequest> getPendingRequests(User user);
    List<User> getConnections(User user);
    String getConnectionStatus(User loggedInUser, User profileUser);
    Map<String, Object> getNetworkDetails(User currentUser);
    Map<String, Object> getFollowersDetails(Long userId);
    Map<String, Object> getFollowingDetails(Long userId);
    void sendConnectionRequest(Long senderId, Long receiverId);
}