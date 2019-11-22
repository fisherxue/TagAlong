package com.tagalong.tagalong;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserAlongAdapter  extends RecyclerView.Adapter<UserAlongAdapter.ViewHolder> {

    private final String TAG = "UserAlongAdapter";
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
                RequestQueue queue = Volley.newRequestQueue(context);
                String url = context.getString(R.string.deleteTrip);
                final Gson gson = new Gson();
                final String tripJson = gson.toJson(usersAlong);
                JSONObject tripJsonObject;
                try {
                    tripJsonObject = new JSONObject((tripJson));
                    Log.d(TAG, "profileJsonObject" + tripJsonObject);
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, tripJsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, "Success");
                        }

                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG, "Error: Could delete Trips");
                            Log.d(TAG, "Error: " + error.getMessage());
                            Toast.makeText(context, "We encountered some error,\nPlease try to delete again page", Toast.LENGTH_LONG).show();
                        }
                    });

                    queue.add(jsonObjectRequest);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return usernames.size();
    }
}