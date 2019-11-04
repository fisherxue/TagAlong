package com.tagalong.tagalong;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TripViewAdapter  extends RecyclerView.Adapter<TripViewAdapter.ViewHolder> {

    private final String TAG = "Trip View Adapter";
    private Context context;
    private List<Trip> tripList;

    public TripViewAdapter(Context context, List<Trip> tripList) {
        this.context = context;
        this.tripList = tripList;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Trip trip = tripList.get(position);
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
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO:implement this method
            }
        });
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }
}
