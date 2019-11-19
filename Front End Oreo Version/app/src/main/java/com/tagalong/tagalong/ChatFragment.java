package com.tagalong.tagalong;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChatFragment extends Fragment {

    private final String TAG = "Proposed Trip Fragment";
    private List<Trip> tripList;
    private View view;
    private Context context;
    private Profile profile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_chat, container, false);
        context = getActivity();
        Bundle inputBundle = getArguments();
        profile = (Profile) inputBundle.getSerializable("profile");
        if (profile.getDriver()){
            initTripList(getString(R.string.getTripList), true);
        }
        else {
            initTripList(getString(R.string.getTripList), false);
        }
        return view;
    }

    private void initTripList(String url, final Boolean isDriver){
        RequestQueue queue = Volley.newRequestQueue(context);
        final Gson gson = new Gson();
        final String profileJson = gson.toJson(profile);
        JSONObject profileJsonObject;

        try {
            profileJsonObject = new JSONObject((profileJson));
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, profileJsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "Received List of Trips for the user");
                    setTripList(response, isDriver);
                    initTripView(isDriver);
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "Error: Could not get list of Trips");
                    Log.d(TAG, "Error: " + error.getMessage());
                    Toast.makeText(context, "We encountered some error,\nPlease reload the page", Toast.LENGTH_LONG).show();
                }
            });

            queue.add(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void initTripView(Boolean isDriver){
        Log.d(TAG,"initializing TripView");
        RecyclerView recyclerView = view.findViewById(R.id.proposed_trip_recycler_view);
        if (isDriver){
            TripProposedDriverAdapter tripViewAdapter = new TripProposedDriverAdapter(context, this.tripList, profile);
            recyclerView.setAdapter(tripViewAdapter);
        }
        else {
            TripProposedRiderAdapter tripViewAdapter = new TripProposedRiderAdapter(context, this.tripList, profile);
            recyclerView.setAdapter(tripViewAdapter);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    private void setTripList (JSONObject response, Boolean isDriver){
        JSONArray tripListIN;
        tripList = new ArrayList<>();
        try {
            tripListIN = response.getJSONArray("trips"); // ASK IAN FOR CORRECT NAME
            for (int i = 0; i < tripListIN.length(); i++){
                if (isDriver){
                    this.tripList.add(new Trip(tripListIN.getJSONObject(i)));
                }
                else {
                    if(!tripListIN.getJSONObject(i).getBoolean("isFulfilled")){
                        this.tripList.add(new Trip(tripListIN.getJSONObject(i)));
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
