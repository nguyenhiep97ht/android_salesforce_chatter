package com.mychatter.android.models;

public class ChatMessage {

    private String id;

    private String body;

    private String senderId;

    private String sentDate;

    private String recipientId;

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSentDate() {
        return sentDate;
    }

    public void setSentDate(String sentDate) {
        this.sentDate = sentDate;
    }


    @Override
    public String toString() {
        return "ChatMessage{" +
                "id='" + id + '\'' +
                ", body='" + body + '\'' +
                ", senderId='" + senderId + '\'' +
                ", sentDate='" + sentDate + '\'' +
                ", recipientId='" + recipientId + '\'' +
                '}';
    }
}
