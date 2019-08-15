package com.mychatter.android.services;

import com.mychatter.android.models.ChatMessage;
import com.mychatter.android.models.ChatUserInfo;

import java.util.List;

public interface IChatterService {
    /** Load contact list from salesforce */
    List<ChatUserInfo> getContactList();

    /** load conversation history from salesforce */
    List<ChatUserInfo> getConversationList();

    /** send message to salesfroce */
    ChatMessage sendMessage(ChatUserInfo conv, ChatMessage msg);

    /** load conversation message */
    List<ChatMessage> loadMessages(ChatUserInfo conv);

    /** Load user info */
    ChatUserInfo getUserInfo();

}
