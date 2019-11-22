package com.tagalong.tagalong.Adapter;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tagalong.tagalong.Models.Profile;
import com.tagalong.tagalong.R;
import com.tagalong.tagalong.Communication.VolleyCallback;
import com.tagalong.tagalong.Communication.VolleyCommunicator;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserAlongAdapter  extends RecyclerView.Adapter<UserAlongAdapter.ViewHolder> {

    private final String TAG = "UserAlong Adapter";
    private Context context;
    private List<String> usernames;
    private Profile profile;

    public UserAlongAdapter(Context context, List<String> usernames, Profile profile) {
        this.context = context;
        this.usernames = usernames;
        this.profile = profile;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private Button viewProfile;
        private TextView userAlong;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            viewProfile = itemView.findViewById(R.id.viewProfile);
            userAlong = itemView.findViewById(R.id.usersAlong);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_along_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final String usersAlong = usernames.get(position);

        holder.userAlong.setText(Html.fromHtml("<b>" + "UserAlong:" + "</b>" + "<br/>" + usersAlong));

        holder.viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = context.getString(R.string.getUserAlongProfile);
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("username",usersAlong);
                VolleyCommunicator communicator = VolleyCommunicator.getInstance(context.getApplicationContext());
                VolleyCallback callback = new VolleyCallback() {
                    @Override
                    public void onSuccess(JSONObject response){
                        Log.d(TAG, "Received list of messages for the chat");

                    }
                    @Override
                    public void onError(String result){
                        Log.d(TAG, "Could not get profile");
                        Log.d(TAG, "Error: " +  result);
                        Toast.makeText(context, "We encountered some error,\nPlease try again", Toast.LENGTH_LONG).show();
                    }
                };

                Log.d(TAG, "Retrieving userAlong profile");
                communicator.VolleyGet(url,callback,headers);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usernames.size();
    }
}