package com.tagalong.tagalong.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tagalong.tagalong.Activity.ViewProfileActivity;
import com.tagalong.tagalong.Models.Profile;
import com.tagalong.tagalong.R;
import com.tagalong.tagalong.Communication.VolleyCallback;
import com.tagalong.tagalong.Communication.VolleyCommunicator;

import org.json.JSONArray;
import org.json.JSONException;
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
                        Log.d(TAG, "Received profile");
                        viewUserAlongSuccess(response);
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

    private void viewUserAlongSuccess(JSONObject response){
        Profile profile = new Profile();
        try {
            profile.setUsername(response.getString("username"));
            profile.setUserID(response.getString("_id"));
            profile.setFirstName(response.getString("firstName"));
            profile.setLastName(response.getString("lastName"));
            profile.setAge(response.getInt("age"));
            profile.setGender(response.getString("gender"));
            profile.setEmail(response.getString("email"));
            profile.setDriver(response.getBoolean("isDriver"));
            profile.setJoinedDate(response.getString("joinedDate"));

            JSONArray jsonArray = response.getJSONArray("interests");
            int [] interests = new int[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++){
                interests[i] = jsonArray.getInt(i);
            }
            profile.setInterests(interests);
        } catch (JSONException e) {
            Log.d(TAG, "Failed to retrieve profile while getting userAlong profile");
            Log.d(TAG, "JSONException: " + e.toString());
        }

        Toast.makeText(context, "Successfully logged in", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(context, ViewProfileActivity.class); //// BRUNO CHANGE THE VIEWPROFILEACTIVITY
        intent.putExtra("profile", profile);
        context.startActivity(intent);
    }
}