package com.org.linkedin.dto;

public class VideoSignalMessage {
    private String type;
    private String senderId;
    private String receiverId;
    private String payload;
    private String senderName;

    public VideoSignalMessage() {}

    public VideoSignalMessage(String type, String senderId, String receiverId, String payload) {
        this.type = type;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.payload = payload;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
}