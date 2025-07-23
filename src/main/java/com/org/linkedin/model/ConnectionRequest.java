package com.org.linkedin.model;

import jakarta.persistence.*;

@Entity
public class ConnectionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User sender;

    @ManyToOne
    private User receiver;

    private String status;

    private Boolean accepted = false;

    public ConnectionRequest() {}

    public ConnectionRequest(User sender, User receiver, String status) {
        this.sender = sender;
        this.receiver = receiver;
        this.status = status;
        this.accepted = "ACCEPTED".equals(status);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        this.accepted = "ACCEPTED".equals(status);
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }
}