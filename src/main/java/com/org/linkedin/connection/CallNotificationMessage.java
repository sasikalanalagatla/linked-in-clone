package com.org.linkedin.connection;

public class CallNotificationMessage {

    private String type;
    private String senderId;
    private String receiverId;
    private String senderName;

    public CallNotificationMessage() {}

    public CallNotificationMessage(String type, String senderId, String receiverId, String senderName) {
        this.type = type;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.senderName = senderName;
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

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
}
