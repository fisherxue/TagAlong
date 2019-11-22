package com.tagalong.tagalong;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ListProposedTripAdapter extends RecyclerView.Adapter<ListProposedTripAdapter.ViewHolder> {

    private final String TAG = "List Proposed Trip Adapter";
    private Context context;
    private List<Trip> tripList;
    private Profile profile;
    private List<JSONObject> objectList;
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();

    public ListProposedTripAdapter(Context context, List<JSONObject> objectList, Profile profile) {
        this.context = context;
        this.objectList = objectList;
        this.profile = profile;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView departurePlace;
        private TextView arrivalPlace;
        private TextView departureTime;
        private TextView arrivalTime;
        private RecyclerView recyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            departurePlace = itemView.findViewById(R.id.departurePlace);
            arrivalPlace = itemView.findViewById(R.id.arrivalPlace);
            departureTime = itemView.findViewById(R.id.departureClock);
            arrivalTime = itemView.findViewById(R.id.arrivalClock);
            recyclerView = itemView.findViewById(R.id.recommended_list_recycler_view);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_list_proposed_trips, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        JSONObject tripObject = null;
        try {
            tripObject = objectList.get(position).getJSONObject("drivertrip");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final Trip trip = new Trip(tripObject);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss, dd MMMM yyyy");

        holder.departurePlace.setText(Html.fromHtml("<b>" + "Departure Place:" + "</b>" + "<br/>" + trip.getDeparturePlace()));
        holder.departureTime.setText(Html.fromHtml("<b>" + "Departure Time:" + "</b>" + "<br/>" + format.format(trip.getDepartureTime())));
        holder.arrivalTime.setText(Html.fromHtml("<b>" + "Arrival Time:" + "</b>" + "<br/>" + format.format(trip.getArrivalTime())));
        holder.arrivalPlace.setText(Html.fromHtml("<b>" + "Arrival Place:" + "</b>" + "<br/>" + trip.getArrivalPlace()));

        JSONArray tripListIN;
        tripList= new ArrayList<>();
        try{
            tripListIN = objectList.get(position).getJSONArray("riderTrips");
            System.out.println(objectList.get(position));
            for (int i = 0; i < tripListIN.length(); i++){
                this.tripList.add(new Trip(tripListIN.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                holder.recyclerView.getContext(),LinearLayoutManager.VERTICAL,false
        );
        linearLayoutManager.setInitialPrefetchItemCount(tripList.size());
        TripProposedDriverAdapter tripProposedDriverAdapter = new TripProposedDriverAdapter(context, tripList, trip.getTripID(), profile);
        holder.recyclerView.setLayoutManager(linearLayoutManager);
        holder.recyclerView.setAdapter(tripProposedDriverAdapter);
        holder.recyclerView.setRecycledViewPool(viewPool);

    }

    @Override
    public int getItemCount() {
        return objectList.size();
    }
}
