package com.mychatter.android.models;

public class ChatUserInfo {
    /** user id */
    private String id =  null;
    /** user name */
    private String name  = null;
    /** user avatar */
    private String avatar = null;
    /** conversation conversationId */
    private String conversationId = null;
    /** conversation last message */
    private String lastMessage = null;

    private String messageId = null;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    @Override
    public String toString() {
        return "ChatUserInfo{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                ", conversationId='" + conversationId + '\'' +
                ", lastMessage='" + lastMessage + '\'' +
                ", messageId='" + messageId + '\'' +
                '}';
    }
}
