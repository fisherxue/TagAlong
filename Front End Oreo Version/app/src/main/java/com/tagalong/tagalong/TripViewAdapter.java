package com.tagalong.tagalong;

import android.content.Context;
import android.content.Intent;
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

import java.text.SimpleDateFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TripViewAdapter  extends RecyclerView.Adapter<TripViewAdapter.ViewHolder> {

    private final String TAG = "Trip View Adapter";
    private Context context;
    private List<Trip> tripList;
    private Profile profile;

    public TripViewAdapter(Context context, List<Trip> tripList, Profile profile) {
        this.context = context;
        this.tripList = tripList;
        this.profile = profile;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private Button map;
        private Button chat;
        private Button delete;
        private TextView departurePlace;
        private TextView arrivalPlace;
        private TextView departureTime;
        private TextView arrivalTime;
        private TextView usersAlong;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            map = itemView.findViewById(R.id.map);
            chat = itemView.findViewById(R.id.chat);
            delete = itemView.findViewById(R.id.delete);
            departurePlace = itemView.findViewById(R.id.departurePlace);
            arrivalPlace = itemView.findViewById(R.id.arrivalPlace);
            departureTime = itemView.findViewById(R.id.departureClock);
            arrivalTime = itemView.findViewById(R.id.arrivalClock);
            usersAlong = itemView.findViewById(R.id.usersAlong);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_trip, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Trip trip = tripList.get(position);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss, dd MMMM yyyy");
        StringBuilder userSB = new StringBuilder();

        for (int i = 0; i < trip.getTaggedUsers().length; i++) {
            userSB.append(trip.getTaggedUsers()[i]).append(",\t");
        }
        String usersAlong = userSB.toString();

        holder.departurePlace.setText("Departure Place: " + trip.getDeparturePlace());
        holder.departureTime.setText("Departure Time: " + format.format(trip.getDepartureTime()));
        holder.arrivalTime.setText("Arrival Time: " + format.format(trip.getArrivalTime()));
        holder.arrivalPlace.setText("Arrival Place" + trip.getArrivalPlace());
        holder.usersAlong.setText("UserAlong" + usersAlong);

        holder.map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO:implement this method
            }
        });

        holder.chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO:implement this method
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("profile", profile);
                intent.putExtra("ID", trip.getRoomID());
                intent.putExtra("users", trip.getTaggedUsers());
                context.startActivity(intent);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestQueue queue = Volley.newRequestQueue(context);
                String url = context.getString(R.string.deleteTrip);
                final Gson gson = new Gson();
                final String tripJson = gson.toJson(trip);
                JSONObject tripJsonObject;
                try {
                    tripJsonObject = new JSONObject((tripJson));
                    Log.d(TAG, "profileJsonObject" + tripJsonObject);
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, tripJsonObject, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, "Trip Deleted");
                            tripList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, tripList.size());
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
        return tripList.size();
    }
}
