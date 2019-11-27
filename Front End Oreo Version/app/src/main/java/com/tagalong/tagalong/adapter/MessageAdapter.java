package com.tagalong.tagalong.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tagalong.tagalong.models.Conversation;
import com.tagalong.tagalong.models.Profile;
import com.tagalong.tagalong.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Adapter to control recycler view display Chat
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

  private final String TAG = "MessageAdapter";
  public static final int MSG_LEFT = 0;
  public static final int MSG_RIGHT = 1;

  private Context context;
  private List<Conversation> conversationList;
  private Profile currentUser;


  public MessageAdapter (Context context, List<Conversation> conversationList, Profile profile) {
    this.context = context;
    this.conversationList = conversationList;
    this.currentUser = profile;
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
    Conversation conversation = conversationList.get(position);
    if (conversation.getUserName().equals(currentUser.getUsername())){
      holder.displayMessage.setText(conversation.getMessage());
    }
    else {
      holder.displayMessage.setText(Html.fromHtml("<b>"+ conversation.getUserName() + ": </b>" + conversation.getMessage()));
    }
  }

  @Override
  public int getItemCount() {
    return conversationList.size();
  }

  @Override
  public int getItemViewType(int position) {
    // Changes the type of view for the user and user along messages (right and left displays)
    if (currentUser.getUsername().equals(conversationList.get(position).getUserName())){
      return MSG_RIGHT;
    }
    else {
      return MSG_LEFT;
    }
  }
}