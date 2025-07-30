package com.org.linkedin.service.impl;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.ConnectionRequest;
import com.org.linkedin.model.User;
import com.org.linkedin.repository.ConnectionRequestRepository;
import com.org.linkedin.service.ConnectionRequestService;
import com.org.linkedin.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ConnectionRequestServiceImpl implements ConnectionRequestService {

    private final ConnectionRequestRepository connectionRequestRepository;
    private final UserService userService;

    public ConnectionRequestServiceImpl(ConnectionRequestRepository connectionRequestRepository, UserService userService) {
        this.connectionRequestRepository = connectionRequestRepository;
        this.userService = userService;
    }

    @Override
    public void sendRequest(User sender, User receiver) {
        if (sender == null || receiver == null) {
            throw new CustomException("INVALID_USER", "Sender or receiver cannot be null");
        }
        if (sender.equals(receiver)) {
            throw new CustomException("INVALID_REQUEST", "Cannot send connection request to self");
        }

        Optional<ConnectionRequest> existingRequest = connectionRequestRepository
                .findConnectionBetweenUsers(sender, receiver);

        if (existingRequest.isPresent()) {
            String status = existingRequest.get().getStatus();
            if ("PENDING".equals(status)) {
                throw new CustomException("REQUEST_ALREADY_EXISTS", "Connection request already pending");
            } else if ("ACCEPTED".equals(status)) {
                throw new CustomException("ALREADY_CONNECTED", "Users are already connected");
            }
        }

        ConnectionRequest request = new ConnectionRequest();
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setStatus("PENDING");
        connectionRequestRepository.save(request);
    }

    @Override
    public void acceptRequest(Long requestId) {
        if (requestId == null) {
            throw new CustomException("INVALID_REQUEST_ID", "Request ID cannot be null");
        }
        ConnectionRequest request = connectionRequestRepository.findById(requestId)
                .orElseThrow(() -> new CustomException("REQUEST_NOT_FOUND", "Request with ID " + requestId + " not found"));

        if (!"PENDING".equals(request.getStatus())) {
            throw new CustomException("INVALID_REQUEST_STATUS", "Request is not in pending status");
        }

        request.setStatus("ACCEPTED");
        connectionRequestRepository.save(request);
    }

    @Override
    public void ignoreRequest(Long requestId) {
        if (requestId == null) {
            throw new CustomException("INVALID_REQUEST_ID", "Request ID cannot be null");
        }
        ConnectionRequest request = connectionRequestRepository.findById(requestId)
                .orElseThrow(() -> new CustomException("REQUEST_NOT_FOUND", "Request with ID " + requestId + " not found"));

        if (!"PENDING".equals(request.getStatus())) {
            throw new CustomException("INVALID_REQUEST_STATUS", "Request is not in pending status");
        }

        request.setStatus("IGNORED");
        connectionRequestRepository.save(request);
    }

    @Override
    public List<ConnectionRequest> getPendingRequests(User user) {
        if (user == null) {
            throw new CustomException("INVALID_USER", "User cannot be null");
        }
        return connectionRequestRepository.findByReceiverAndStatus(user, "PENDING");
    }

    @Override
    public List<User> getConnections(User user) {
        if (user == null) {
            throw new CustomException("INVALID_USER", "User cannot be null");
        }
        List<ConnectionRequest> acceptedRequests = connectionRequestRepository.findAcceptedConnectionsForUser(user);
        List<User> connections = new ArrayList<>();
        for (ConnectionRequest request : acceptedRequests) {
            if (request.getSender().equals(user)) {
                connections.add(request.getReceiver());
            } else {
                connections.add(request.getSender());
            }
        }
        return connections;
    }

    @Override
    public String getConnectionStatus(User loggedInUser, User profileUser) {
        if (loggedInUser.getUserId().equals(profileUser.getUserId())) {
            return "SELF";
        }

        Optional<ConnectionRequest> pendingRequest = connectionRequestRepository
                .findPendingConnectionBetweenUsers(loggedInUser, profileUser);

        if (pendingRequest.isPresent()) {
            return "PENDING";
        }

        Optional<ConnectionRequest> acceptedRequest = connectionRequestRepository
                .findAcceptedConnectionBetweenUsers(loggedInUser, profileUser);

        if (acceptedRequest.isPresent()) {
            return "CONNECTED";
        }
        return "NONE";
    }

    @Override
    public Map<String, Object> getNetworkDetails(User currentUser) {
        if (currentUser == null) {
            throw new CustomException("INVALID_USER", "User cannot be null");
        }
        Map<String, Object> modelAttributes = new HashMap<>();
        List<ConnectionRequest> pendingRequests = getPendingRequests(currentUser);
        List<User> connections = getConnections(currentUser);
        List<User> followers = userService.getFollowers(currentUser);
        List<User> following = userService.getFollowing(currentUser);

        modelAttributes.put("requests", pendingRequests != null ? pendingRequests : new ArrayList<>());
        modelAttributes.put("connections", connections != null ? connections : new ArrayList<>());
        modelAttributes.put("followers", followers != null ? followers : new ArrayList<>());
        modelAttributes.put("following", following != null ? following : new ArrayList<>());
        modelAttributes.put("currentUserId", currentUser.getUserId());

        return modelAttributes;
    }

    @Override
    public Map<String, Object> getFollowersDetails(Long userId) {
        if (userId == null) {
            throw new CustomException("INVALID_USER_ID", "User ID cannot be null");
        }
        User user = userService.getUserById(userId);
        Map<String, Object> modelAttributes = new HashMap<>();
        List<User> followers = userService.getFollowers(user);
        modelAttributes.put("followers", followers != null ? followers : new ArrayList<>());
        modelAttributes.put("currentUserId", userId);
        return modelAttributes;
    }

    @Override
    public Map<String, Object> getFollowingDetails(Long userId) {
        if (userId == null) {
            throw new CustomException("INVALID_USER_ID", "User ID cannot be null");
        }
        User user = userService.getUserById(userId);
        Map<String, Object> modelAttributes = new HashMap<>();
        List<User> following = userService.getFollowing(user);
        modelAttributes.put("following", following != null ? following : new ArrayList<>());
        modelAttributes.put("currentUserId", userId);
        return modelAttributes;
    }

    @Override
    public void sendConnectionRequest(Long senderId, Long receiverId) {
        if (senderId == null || receiverId == null) {
            throw new CustomException("INVALID_USER_ID", "Sender or receiver ID cannot be null");
        }
        User sender = userService.getUserById(senderId);
        User receiver = userService.getUserById(receiverId);
        sendRequest(sender, receiver);
    }
}