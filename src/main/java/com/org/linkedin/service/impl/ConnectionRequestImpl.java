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

    private final ConnectionRequestRepository repository;

    public ConnectionRequestImpl(ConnectionRequestRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<ConnectionRequest> getPendingRequests(User user) {
        return repository.findByReceiverAndAcceptedFalse(user);
    }

    @Override
    public void sendRequest(User sender, User receiver) {
        if (repository.existsBySenderAndReceiver(sender, receiver)) return;

        ConnectionRequest request = new ConnectionRequest();
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setAccepted(false);
        repository.save(request);
    }

    @Override
    public void acceptRequest(Long requestId) {
        ConnectionRequest request = repository.findById(requestId).orElse(null);
        if (request != null) {
            request.setAccepted(true);
            repository.save(request);
        }
    }

    @Override
    public List<User> getConnections(User user) {
        List<ConnectionRequest> accepted = repository.findBySenderAndAcceptedTrueOrReceiverAndAcceptedTrue(user, user);
        List<User> connections = new ArrayList<>();

        for (ConnectionRequest req : accepted) {
            if (req.getSender().getUserId().equals(user.getUserId())) {
                connections.add(req.getReceiver());
            } else {
                connections.add(req.getSender());
            }
        }
        return connections;
    }


}
