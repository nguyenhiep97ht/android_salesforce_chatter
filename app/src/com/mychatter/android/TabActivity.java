package com.mychatter.android;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.tabs.TabLayout;
import com.mychatter.android.models.ChatUserInfo;
import com.mychatter.android.services.ChatterService;
import com.mychatter.android.ui.chat.ChatActivity;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.ui.SalesforceActivity;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TabActivity extends SalesforceActivity implements View.OnClickListener, TabLayout.OnTabSelectedListener {
    private static int TAG_USER = 0;
    private static int TAG_CONV = 1;

    public class ChatListAdapter extends ArrayAdapter<ChatUserInfo> {
        public ChatListAdapter(@NonNull Context context) {
            super(context, 0, new ArrayList<ChatUserInfo>());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            ChatUserInfo user = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_item, parent, false);
            }
            // Lookup view for data population
            try {
                URL url = new URL(user.getAvatar());
                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                ImageView avatar = convertView.findViewById(R.id.avatar);
                avatar.setImageBitmap(bmp);
            } catch(IOException e) {
                e.printStackTrace();
            }
            TextView textName =  convertView.findViewById(R.id.name);
            textName.setText(user.getName());
            convertView.setTag(R.string.tag_user, user.getId());
            convertView.setTag(R.string.tag_conv, user.getConversationId());
            convertView.setClickable(true);
            convertView.setOnClickListener((View.OnClickListener) this.getContext());
            return convertView;
        }

    }


    private ChatListAdapter myAdapter = null;
    private ChatterService chatterService = null;
    private RestClient client = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * fix async call
         */

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        this.setContentView(R.layout.activity_tab);
        TabLayout tabs = this.findViewById(R.id.tabs);
        tabs.addOnTabSelectedListener(this);
        ListView list = this.findViewById(R.id.lstChatContact);
        myAdapter = new ChatListAdapter(this);
        list.setAdapter(myAdapter);

    }


    @Override
    public void onResume(RestClient client) {
        // Keeping reference to rest client
        this.client = client;
        //System.out.println("Tab Act: "+client);
        this.chatterService = new ChatterService(client);
        List<ChatUserInfo> data = chatterService.getContactList();
        myAdapter.clear();
        if (data != null) {
            this.myAdapter.addAll(data);
        }
    }

    public void onClick(View v) {
        String userId = (String) v.getTag(R.string.tag_user);
        String convId = (String) v.getTag(R.string.tag_conv);
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("convId", convId);
        this.startActivity(intent);
    }

    public void onTabSelected(TabLayout.Tab tab) {
        if (chatterService == null) {
            return;
        }
        List<ChatUserInfo> data = null;
        switch(tab.getPosition()) {
            case 0:
                data = chatterService.getContactList();
                break;
            default:
                data = chatterService.getConversationList();
                break;
        }
        myAdapter.clear();
        if (data != null) {
            this.myAdapter.addAll(data);
        }
    }

    public void onTabUnselected(TabLayout.Tab tab) {}

    public void onTabReselected(TabLayout.Tab tab) {}

}