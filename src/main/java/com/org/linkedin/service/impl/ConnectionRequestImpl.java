package com.org.linkedin.service.impl;

import com.org.linkedin.exception.CustomException;
import com.org.linkedin.model.ConnectionRequest;
import com.org.linkedin.model.User;
import com.org.linkedin.repository.ConnectionRequestRepository;
import com.org.linkedin.service.ConnectionRequestService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ConnectionRequestImpl implements ConnectionRequestService {

    private final ConnectionRequestRepository connectionRequestRepository;

    public ConnectionRequestImpl(ConnectionRequestRepository connectionRequestRepository) {
        this.connectionRequestRepository = connectionRequestRepository;
    }

    @Override
    public void sendRequest(User sender, User receiver) {
        if (sender == null || receiver == null) {
            throw new CustomException("INVALID_USER", "Sender or receiver cannot be null");
        }
        if (sender.equals(receiver)) {
            throw new CustomException("INVALID_REQUEST", "Cannot send connection request to self");
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
        List<ConnectionRequest> acceptedRequests = connectionRequestRepository.findBySenderOrReceiverAndStatus(user, user, "ACCEPTED");
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
    public Optional<ConnectionRequest> getRequestById(Long requestId) {
        if (requestId == null) {
            throw new CustomException("INVALID_REQUEST_ID", "Request ID cannot be null");
        }
        return connectionRequestRepository.findById(requestId);
    }
}