package com.mychatter.android.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.mychatter.android.R;
import com.mychatter.android.models.ChatMessage;
import com.mychatter.android.models.ChatUserInfo;
import com.mychatter.android.services.ChatterService;
import com.mychatter.android.ui.adapter.ChatAdapter;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.ui.SalesforceActivity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ChatActivity extends SalesforceActivity {
    private EditText editText;
    private RecyclerView mRecyclerView;
    private ChatterService chatterService;
    private RestClient client = null;
    private String userId;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = this.getIntent();
        userId = intent.getStringExtra("userId");

        mSwipeRefreshLayout = findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        doYourUpdate();
                    }
                }
        );
        mRecyclerView = findViewById(R.id.messages_view);
        //mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);


    }
    private void doYourUpdate() {
        // TODO implement a refresh
        finish();
        startActivity(getIntent());
        mSwipeRefreshLayout.setRefreshing(false); // Disables the refresh icon
    }

    @Override
    public void onResume() {
        super.onResume();

        mAdapter = new ChatAdapter(goiData(),userId);
        mRecyclerView.setAdapter(mAdapter);

    }
    public List<ChatMessage> goiData(){
        List<ChatUserInfo> chatUserInfoList = chatterService.getConversationList();
        for(ChatUserInfo chatUserInfo : chatUserInfoList){
            if (chatUserInfo.getId().equals(userId)){
                List<ChatMessage> chatMessageList = chatterService.loadMessages(chatUserInfo);
                Collections.sort(chatMessageList, new Comparator<ChatMessage>() {
                    @Override
                    public int compare(ChatMessage o1, ChatMessage o2) {
                        return o1.getSentDate().compareTo(o2.getSentDate());
                    }
                });
                for(ChatMessage chatMessage: chatMessageList){
                    System.out.println(chatMessage.toString());
                }
                return chatMessageList;
            }
        }
        return null;
    }

    public void sendMessage(View view) {
        editText = findViewById(R.id.editText);
        String message = editText.getText().toString();
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setBody(message);
        System.out.println(userId);
        List<ChatUserInfo> chatUserInfoList = chatterService.getContactList();

        for (ChatUserInfo chatUserInfo : chatUserInfoList) {
            if (chatUserInfo.getId().equals(userId)) {
                chatterService.sendMessage(chatUserInfo, chatMessage);
                break;
            }
        }
        editText.getText().clear();
        mAdapter = new ChatAdapter(goiData(),userId);
        if(goiData() != null){
            System.out.println("goi data ko null");
            System.out.println("goi data"+goiData().size());
        }else{
            System.out.println("goi data null");
        }
        mRecyclerView.setAdapter(mAdapter);
        //mRecyclerView.scrollToPosition(goiData().size() - 1);


    }


    @Override
    public void onResume(RestClient client) {
        this.client = client;
        System.out.println("Tab Act: " + client);
        this.chatterService = new ChatterService(client);

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void recreate() {
        super.recreate();
    }
}
