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
import io.opencensus.common.ToDoubleFunction;

public class TripProposedDriverAdapter extends RecyclerView.Adapter<TripProposedDriverAdapter.ViewHolder> {

    private final String TAG = "Trip View Adapter";
    private Context context;
    private List<Trip> tripList;
    private Profile profile;

    public TripProposedDriverAdapter(Context context, List<Trip> tripList, Profile profile) {
        this.context = context;
        this.tripList = tripList;
        this.profile = profile;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private Button map;
        private Button accept;
        private Button reject;
        private TextView departurePlace;
        private TextView arrivalPlace;
        private TextView departureTime;
        private TextView arrivalTime;
        private TextView usersAlong;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            map = itemView.findViewById(R.id.map);
            accept = itemView.findViewById(R.id.accept);
            reject = itemView.findViewById(R.id.reject);
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
        View view = LayoutInflater.from(context).inflate(R.layout.list_proposed_trip_driver, parent, false);
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
                Intent intent = new Intent(context, TripDisplayActivity.class);
                intent.putExtra("tripRoute", trip.getTripRoute().toString());
                context.startActivity(intent);
            }
        });

        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODo: Write Acceptance
            }
        });

        holder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODo: Write Rejection
            }
        });
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }
}
