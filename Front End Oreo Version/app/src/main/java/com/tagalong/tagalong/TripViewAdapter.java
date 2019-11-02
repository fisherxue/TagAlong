package com.tagalong.tagalong;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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

        Button map;
        Button chat;
        Button delete;
        TextView departurePlace;
        TextView arrivalPlace;
        TextView departureTime;
        TextView arrivalTime;
        TextView usersAlong;

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

        holder.departurePlace.setText("Departure Place: ");
        holder.departureTime.setText("Departure Time");
        holder.arrivalTime.setText("Arrival Time");
        holder.arrivalPlace.setText("Arrival Place");
        holder.usersAlong.setText("UserAlong");

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
