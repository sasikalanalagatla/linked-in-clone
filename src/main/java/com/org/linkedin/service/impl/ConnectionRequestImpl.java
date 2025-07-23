package com.org.linkedin.service.impl;

import com.org.linkedin.model.ConnectionRequest;
import com.org.linkedin.model.User;
import com.org.linkedin.repository.ConnectionRequestRepository;
import com.org.linkedin.service.ConnectionRequestService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConnectionRequestImpl implements ConnectionRequestService {

    private final ConnectionRequestRepository connectionRequestRepository;

    public ConnectionRequestImpl(ConnectionRequestRepository connectionRequestRepository) {
        this.connectionRequestRepository = connectionRequestRepository;
    }

    @Override
    public void sendRequest(User sender, User receiver) {
        if (sender.equals(receiver)) {
            return; // Prevent self-connection
        }
        ConnectionRequest request = new ConnectionRequest();
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setStatus("PENDING");
        connectionRequestRepository.save(request);
    }

    @Override
    public void acceptRequest(Long requestId) {
        ConnectionRequest request = connectionRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
        request.setStatus("ACCEPTED");
        connectionRequestRepository.save(request);
    }

    @Override
    public void ignoreRequest(Long requestId) {
        ConnectionRequest request = connectionRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
        request.setStatus("IGNORED");
        connectionRequestRepository.save(request);
    }

    @Override
    public List<ConnectionRequest> getPendingRequests(User user) {
        return connectionRequestRepository.findByReceiverAndStatus(user, "PENDING");
    }

    @Override
    public List<User> getConnections(User user) {
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
}