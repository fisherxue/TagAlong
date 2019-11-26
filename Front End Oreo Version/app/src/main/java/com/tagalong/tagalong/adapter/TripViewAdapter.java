package com.tagalong.tagalong.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.util.TimingLogger;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tagalong.tagalong.activity.MessageActivity;
import com.tagalong.tagalong.activity.TripDisplayActivity;
import com.tagalong.tagalong.models.Profile;
import com.tagalong.tagalong.models.Trip;
import com.tagalong.tagalong.R;
import com.tagalong.tagalong.communication.VolleyCallback;
import com.tagalong.tagalong.communication.VolleyCommunicator;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TripViewAdapter  extends RecyclerView.Adapter<TripViewAdapter.ViewHolder> {

    private final String TAG = "TripViewAdapter";
    private Context context;
    private List<Trip> tripList;
    private Profile profile;
    private List<String> useralonglist;

    private TimingLogger timingLogger;

    public TripViewAdapter(Context context, List<Trip> tripList, Profile profile) {
        this.context = context;
        this.tripList = tripList;
        this.profile = profile;
        timingLogger = new TimingLogger(TAG, "Trip View Adapter");
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private Button map;
        private Button chat;
        private Button delete;
        private TextView departurePlace;
        private TextView arrivalPlace;
        private TextView departureTime;
        private TextView arrivalTime;
        private RecyclerView recyclerView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            map = itemView.findViewById(R.id.map);
            chat = itemView.findViewById(R.id.chat);
            delete = itemView.findViewById(R.id.delete);
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
        View view = LayoutInflater.from(context).inflate(R.layout.list_trip, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Trip trip = tripList.get(position);
        timingLogger.addSplit("Starting to setup trip cards");
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss, dd MMMM yyyy");

        useralonglist = new ArrayList<>();
        for (int i = 0; i < trip.getTaggedUsers().length; i++) {
            useralonglist.add(trip.getTaggedUsers()[i]);
        }

        holder.departurePlace.setText(Html.fromHtml("<b>" + "Departure Place:" + "</b>" + "<br/>" + trip.getDeparturePlace()));
        holder.departureTime.setText(Html.fromHtml("<b>" + "Departure Time:" + "</b>" + "<br/>" + format.format(trip.getDepartureTime())));
        holder.arrivalTime.setText(Html.fromHtml("<b>" + "Arrival Time:" + "</b>" + "<br/>" + format.format(trip.getArrivalTime())));
        holder.arrivalPlace.setText(Html.fromHtml("<b>" + "Arrival Place:" + "</b>" + "<br/>" + trip.getArrivalPlace()));

        UserAlongAdapter userAlongAdapter = new UserAlongAdapter(context, useralonglist, profile);
        holder.recyclerView.setAdapter(userAlongAdapter);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));

        holder.map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TripDisplayActivity.class);
                intent.putExtra("tripRoute", trip.getTripRoute().toString());
                context.startActivity(intent);
            }
        });

        holder.chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

                String url = context.getString(R.string.deleteTrip);
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("userID",profile.getUserID());
                headers.put("tripID",trip.getTripID());

                VolleyCommunicator communicator = VolleyCommunicator.getInstance(context.getApplicationContext());
                VolleyCallback callback = new VolleyCallback() {
                    @Override
                    public void onSuccess(JSONObject response){
                        Log.d(TAG, "Trip Deleted");
                        tripList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, tripList.size());
                    }

                    @Override
                    public void onError(String result){
                        Log.d(TAG, "Could not delete trips");
                        Log.d(TAG, "Error: " + result);
                        Toast.makeText(context, "We encountered some error,\nPlease try to delete again page", Toast.LENGTH_LONG).show();

                    }
                };
                timingLogger.addSplit("Deleted a trip");
                communicator.VolleyDelete(url,callback,headers);
            }
        });
        timingLogger.addSplit("Done setting all trip cards");
        timingLogger.dumpToLog();
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }
}
