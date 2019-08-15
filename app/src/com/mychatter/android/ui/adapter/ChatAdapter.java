package com.mychatter.android.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mychatter.android.R;
import com.mychatter.android.models.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {
    private List<ChatMessage> lstChatMessages;

    private Context mContext;
    private String userId;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView myChatMessage, theirChatMessage;
        public View theirAvt;
        private RelativeLayout mMyMessage, theirMessage;

        public MyViewHolder(View v) {
            super(v);
            myChatMessage = v.findViewById(R.id.myMessage);
            theirChatMessage = v.findViewById(R.id.their_message);
            mMyMessage = v.findViewById(R.id.myMessageLayout);
            theirMessage = v.findViewById(R.id.theirMessageLayout);
        }
    }

    public ChatAdapter(List<ChatMessage> lstChatMessages, String userId) {
        this.lstChatMessages = lstChatMessages;
        this.userId = userId;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.my_message, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (!lstChatMessages.get(position).getSenderId().equals(userId)){
            holder.theirChatMessage.setVisibility(View.GONE);
            holder.myChatMessage.setText(lstChatMessages.get(position).getBody());
        } else {
            holder.mMyMessage.setVisibility(View.GONE);
            holder.theirChatMessage.setText(lstChatMessages.get(position).getBody());
        }
    }

    @Override
    public int getItemCount() {
        return  lstChatMessages == null ? 0 : lstChatMessages.size();
    }


}
