package com.tagalong.tagalong;

import android.content.Context;
import android.util.TimingLogger;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private final String TAG = "MessageAdapter";
    public static final int MSG_LEFT = 0;
    public static final int MSG_RIGHT = 1;

    private Context context;
    private List<Conversation> conversationList;
    private Profile currentUser;

    private TimingLogger timingLogger;

    public MessageAdapter (Context context, List<Conversation> conversationList, Profile profile) {
        this.context = context;
        this.conversationList = conversationList;
        this.currentUser = profile;
        timingLogger = new TimingLogger(TAG, "Message Adapter");
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView displayMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            displayMessage = itemView.findViewById(R.id.displayMessage);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.message_right, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.message_left, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        timingLogger.addSplit("Starting to setup conversations");
        Conversation conversation = conversationList.get(position);
        if (conversation.getUserName().equals(currentUser.getUserName())){
            holder.displayMessage.setText(conversation.getMessage());
        }
        else {
            holder.displayMessage.setText(conversation.getUserName() + ": " + conversation.getMessage());
        }
        timingLogger.addSplit("Done Setting All Conversations");
        timingLogger.dumpToLog();
        //timingLogger.reset();

    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (currentUser.getUserName().equals(conversationList.get(position).getUserName())){
            return MSG_RIGHT;
        }
        else {
            return MSG_LEFT;
        }
    }
}
