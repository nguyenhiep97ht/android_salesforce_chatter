package com.mychatter.android.services;

import com.mychatter.android.models.ChatMessage;
import com.mychatter.android.models.ChatUserInfo;
import com.salesforce.androidsdk.accounts.UserAccountManager;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestRequest.RestMethod;
import com.salesforce.androidsdk.rest.RestResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class ChatterService implements IChatterService {
    public static final String CHATTER_SERVICE_ROOT = "/services/data/v45.0/chatter/users";
    public static final String CONTACT_PATH = CHATTER_SERVICE_ROOT + "/";
    public static final String CONVERSATION_PATH = CHATTER_SERVICE_ROOT + "/me/conversations";
    public static final String USER_INFO_PATH = CHATTER_SERVICE_ROOT + "/me";
    public static final String MESSAGE_PATH = CHATTER_SERVICE_ROOT + "/me/messages/";

    private RestClient restClient = null;
    public static String currentId;

    public ChatterService(RestClient restClient) {
        this.restClient = restClient;
    }


    @Override
    public List<ChatUserInfo> getContactList() {
        RestRequest request = new RestRequest(RestMethod.GET, CONTACT_PATH);
        try {
            RestResponse response = restClient.sendSync(request);
            JSONObject json = response.asJSONObject();
            JSONArray jsArray = json.getJSONArray("users");
            if (jsArray == null || jsArray.length() == 0) {
                return null;
            }
            List<ChatUserInfo> lst = new ArrayList<>();
            // iterator
            for (int i = 0, n = jsArray.length(); i < n; i++) {
                JSONObject jsObj = jsArray.optJSONObject(i);
                ChatUserInfo u = new ChatUserInfo();
                u.setId(jsObj.getString("id"));
                u.setName(jsObj.getString("displayName"));
                u.setAvatar(jsObj.getJSONObject("photo").getString("standardEmailPhotoUrl"));
                lst.add(u);
            }

            return lst;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<ChatUserInfo> getConversationList() {
        RestRequest request = new RestRequest(RestMethod.GET, CONVERSATION_PATH);
        try {
            RestResponse response = restClient.sendSync(request);
            JSONObject json = response.asJSONObject();
            JSONArray jsArray = json.optJSONArray("conversations");
            if (jsArray == null || jsArray.length() == 0) {
                return null;
            }
            List<ChatUserInfo> lst = new ArrayList<>();
            // iterator
            for (int i = 0, n = jsArray.length(); i < n; i++) {
                JSONObject jsObj = jsArray.optJSONObject(i);
                ChatUserInfo c = new ChatUserInfo();
                JSONObject jsU = getChatUser(jsObj.getJSONArray("members"));
                c.setConversationId(jsObj.optString("id"));
                c.setLastMessage(getMsgBody(jsObj.optJSONObject("latestMessage")));
                // set user info
                c.setId(jsU.optString("id"));
                //c.setMessageId(jsU.optString("id"));
                c.setName(jsU.optString("displayName"));
                c.setAvatar(jsU.optJSONObject("photo").optString("standardEmailPhotoUrl"));
                lst.add(c);
            }

            return lst;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public ChatMessage sendMessage(ChatUserInfo conv, ChatMessage msg) {
        JSONObject body = new JSONObject();
        String path = "{\"body\":\"" + msg.getBody() + "\",\"recipients\":[\"" + conv.getId() + "\"]}";
        RequestBody requestBody = (RequestBody) RequestBody.create(MediaType.parse("application/json"), path);

        try {
            //System.out.println(conv.getId());
            body.put("body", msg.getBody());
            body.put("recipients", Arrays.asList(conv.getId()));
            //body.put("inReplyTo", conv.getConversationId());



            RestRequest request = new RestRequest(RestMethod.POST, MESSAGE_PATH, requestBody);
            RestResponse resp = restClient.sendSync(request);
            System.out.println(body);

            return convertMessage(resp.asJSONObject());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public List<ChatMessage> loadMessages(ChatUserInfo with) {
        String url = MESSAGE_PATH;
//        if (with.getConversationId() != null) {
//            url += with.getConversationId();
//        } else {
//            url += "?recipients=" + with.getId();
//        }
        try {
            RestRequest request = new RestRequest(RestMethod.GET, url);

            RestResponse response = restClient.sendSync(request);
            currentId = restClient.getClientInfo().userId;


            JSONArray messages = response.asJSONObject().getJSONArray("messages");


            List<ChatMessage> listMsg = new ArrayList<>();
            for (int i = 0; i < messages.length(); i++) {
                ChatMessage chatMessage = convertMessage(messages.getJSONObject(i));
                if(chatMessage.getRecipientId().equals(with.getId()) && chatMessage.getSenderId().equals(currentId))
                listMsg.add(chatMessage);
            }

            for(int i = 0; i < messages.length(); i++){

                ChatMessage chatMessage = convertMessage(messages.getJSONObject(i));

                if(chatMessage.getRecipientId().equals(currentId) && chatMessage.getSenderId().equals(with.getId())){

                    if(chatMessage != null){
                        System.out.println(chatMessage.toString());
                        listMsg.add(chatMessage);
                    }else System.out.println("message null");

                }


            }


            for(ChatMessage chatMessage: listMsg){
                System.out.println(chatMessage.toString());
            }
            return listMsg;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public ChatUserInfo getUserInfo() {
        try {
            RestRequest request = new RestRequest(RestMethod.GET, USER_INFO_PATH);
            RestResponse response = restClient.sendSync(request);
            JSONObject json = response.asJSONObject();
            ChatUserInfo conv = new ChatUserInfo();
            conv.setId(json.optString("id"));
            conv.setName(json.optString("displayName"));
            conv.setAvatar(json.optJSONObject("photo").optString("standardEmailPhotoUrl"));
            return conv;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * get conversation user
     */
    private static JSONObject getChatUser(JSONArray users) throws Exception {
        if (users == null || users.length() == 0) {
            return null;
        }
        String currentUserId = UserAccountManager.getInstance().getStoredUserId();
        if (currentUserId == null) {
            return null;
        }
        for (int i = 0, n = users.length(); i < n; i++) {
            JSONObject u = users.getJSONObject(i);
            if (!currentUserId.equals(u.getString("id"))) {
                return u;
            }

        }
        return null;
    }

    /**
     * get message text
     */
    private static String getMsgBody(JSONObject message) throws Exception {
        return message.getJSONObject("body").getString("text");
    }

    private static boolean isCurrentChat(JSONObject msg, String userId) throws Exception {
        if (userId == null || msg == null) {
            return false;
        }
        JSONArray recipients = msg.getJSONArray("recipients");
        for (int i = 0, n = recipients.length(); i < n; i++) {
            JSONObject o = recipients.getJSONObject(i);
            if (userId.equals(o.getString("id"))) {
                return true;
            }
        }
        return false;
    }

    /**
     * convert salesforce chatter message to client message format
     */
    private static ChatMessage convertMessage(JSONObject msg) throws Exception {
        ChatMessage res = new ChatMessage();
        res.setId(msg.optString("id"));
        res.setBody(getMsgBody(msg));
        res.setSentDate(msg.optString("sentDate"));
        JSONObject sender = msg.optJSONObject("sender");
        JSONArray recipients = msg.optJSONArray("recipients");


        res.setSenderId(sender.optString("id"));

        JSONObject recipient1 = (JSONObject) recipients.get(0);
        JSONObject recipient2 = (JSONObject) recipients.get(1);
        String id1 = recipient1.optString("id");
        String id2 = recipient2.optString("id");
        if(sender.optString("id").equals(id1)){
            res.setRecipientId(id2);
        }else
            res.setRecipientId(id1);
        return res;
    }
}
