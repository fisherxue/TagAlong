package com.tagalong.tagalong.adapter;

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

import com.google.gson.JsonObject;
import com.tagalong.tagalong.models.Profile;
import com.tagalong.tagalong.models.Trip;
import com.tagalong.tagalong.R;
import com.tagalong.tagalong.activity.TripDisplayActivity;
import com.tagalong.tagalong.communication.VolleyCallback;
import com.tagalong.tagalong.communication.VolleyCommunicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Adapter to control recycler view display of each trip proposed to driver
 * Displays list of driver trips each with a list of proposed trips inside.
 * Uses UserAlongAdapter (sub adapter) to display list of users along in the proposed trip.
 */
public class TripProposedDriverAdapter extends RecyclerView.Adapter<TripProposedDriverAdapter.ViewHolder> {

    private final String TAG = "Trip Proposed View Adapter";
    private Context context;
    private List<Trip> tripList;
    private Profile profile;
    private Trip trip;

    public TripProposedDriverAdapter(Context context, List<Trip> tripList, Trip trip, Profile profile) {
        this.context = context;
        this.tripList = tripList;
        this.profile = profile;
        this.trip = trip;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private Button map;
        private Button accept;
        private TextView departurePlace;
        private TextView arrivalPlace;
        private TextView departureTime;
        private TextView arrivalTime;
        private RecyclerView recyclerView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            map = itemView.findViewById(R.id.map);
            accept = itemView.findViewById(R.id.accept);
            departurePlace = itemView.findViewById(R.id.departurePlace);
            arrivalPlace = itemView.findViewById(R.id.arrivalPlace);
            departureTime = itemView.findViewById(R.id.departureClock);
            arrivalTime = itemView.findViewById(R.id.arrivalClock);
            recyclerView = itemView.findViewById(R.id.user_along_recycler_view);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_proposed_trip_driver, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Trip riderTrip = tripList.get(position);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss, dd MMMM yyyy");
        List<String> useralonglist;
        useralonglist = new ArrayList<>();
        useralonglist.add(riderTrip.getUsername());

        //Fill trip card fields to display with relevant details
        holder.departurePlace.setText(Html.fromHtml("<b>" + "Departure Place:" + "</b>" + "<br/>" + riderTrip.getDeparturePlace()));
        holder.departureTime.setText(Html.fromHtml("<b>" + "Departure Time:" + "</b>" + "<br/>" + format.format(riderTrip.getDepartureTime())));
        holder.arrivalTime.setText(Html.fromHtml("<b>" + "Arrival Time:" + "</b>" + "<br/>" + format.format(riderTrip.getArrivalTime())));
        holder.arrivalPlace.setText(Html.fromHtml("<b>" + "Arrival Place:" + "</b>" + "<br/>" + riderTrip.getArrivalPlace()));

        //Use the userAlongAdapter to display list of users along.
        UserAlongAdapter userAlongAdapter = new UserAlongAdapter(context, useralonglist);
        holder.recyclerView.setAdapter(userAlongAdapter);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));

        holder.map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TripDisplayActivity.class);
                intent.putExtra("tripRoute", riderTrip.getTripRoute().toString());
                context.startActivity(intent);
            }
        });

        //Post request to the trip accepted by the driver.
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Accepting Trip");
                String url = context.getString(R.string.acceptTrip);
                for (int i = 0; i < trip.getTaggedUsers().length; i++) {
                    System.out.println(trip.getTaggedUsers()[i]);
                }

                if (trip.getTaggedUsers().length < profile.getCarCapacity()) {
                    System.out.println(profile.getCarCapacity());
                    for (int i = 0; i < trip.getTaggedUsers().length; i++) {
                        System.out.println(trip.getTaggedUsers()[i]);
                    }
                    JsonObject acceptTrip = new JsonObject();
                    acceptTrip.addProperty("usertripID", riderTrip.getTripID());
                    acceptTrip.addProperty("tripID", trip.getTripID());
                    acceptTrip.addProperty("userID", profile.getUserID());
                    JSONObject acceptTripJson;

                    VolleyCommunicator communicator = VolleyCommunicator.getInstance(context.getApplicationContext());
                    VolleyCallback callback = new VolleyCallback() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            Log.d(TAG, "Trip accepted success");
                            tripList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, tripList.size());
                        }

                        @Override
                        public void onError(String result) {
                            Log.d(TAG, "Could not accept trip");
                            Log.d(TAG, "Error: " + result);
                            Toast.makeText(context, "We encountered some error,\nPlease try again", Toast.LENGTH_LONG).show();
                        }

                    };
                    try {
                        acceptTripJson = new JSONObject(acceptTrip.toString());
                        communicator.volleyPost(url, acceptTripJson, callback);
                    } catch (JSONException e) {
                        Log.d(TAG, "Error making accepted-trip JSONObject");
                        Log.d(TAG, "JSONException: " + e.toString());
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(context, "You cannot accept this trip!\n Your car already reached full capacity!", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Cannot accept trip due to full car capacity");
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }
}
