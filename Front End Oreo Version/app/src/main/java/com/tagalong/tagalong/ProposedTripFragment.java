package com.tagalong.tagalong;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ProposedTripFragment extends Fragment {

    private final String TAG = "Proposed Trip Fragment";
    private List<Trip> tripList;
    private List<JSONObject> proposedList;
    private View view;
    private Context context;
    private Profile profile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_proposed_trips, container, false);
        context = getActivity();
        Bundle inputBundle = getArguments();
        profile = (Profile) inputBundle.getSerializable("profile");
        if (profile.getDriver()){
            initTripList(getString(R.string.getRecommendedTrips), true);
        }
        else {
            initTripList(getString(R.string.getTripList), false);
        }
        return view;
    }

    private void initTripList(String url, final Boolean isDriver){

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("userID",profile.getUserID());

        VolleyCommunicator communicator = VolleyCommunicator.getInstance(context.getApplicationContext());
        VolleyCallback callback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response){
                Log.d(TAG, "Received list of trips for the user");
                setTripList(response, isDriver);
                initTripView(isDriver);
            }

            @Override
            public void onError(String result){
                Log.d(TAG, "Could not get list of Trips");
                Log.d(TAG, "Error: " + result);
                Toast.makeText(context, "We encountered some error,\nPlease reload the page", Toast.LENGTH_LONG).show();

            }
        };
        communicator.VolleyGet(url,callback,headers);

    }
    private void initTripView(Boolean isDriver){
        Log.d(TAG,"initializing TripView");
        RecyclerView recyclerView = view.findViewById(R.id.proposed_trip_recycler_view);
        if (isDriver){
            ListProposedTripAdapter listProposedTripAdapter = new ListProposedTripAdapter(context, this.proposedList, profile);
            recyclerView.setAdapter(listProposedTripAdapter);
        }
        else {
            TripProposedRiderAdapter tripViewAdapter = new TripProposedRiderAdapter(context, this.tripList, profile);
            recyclerView.setAdapter(tripViewAdapter);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    private void setTripList (JSONObject response, Boolean isDriver){
        JSONArray inputTripList;
        if (isDriver) {
            proposedList = new ArrayList<>();
            try{
                inputTripList = response.getJSONArray("trips");
                for (int i = 0; i < inputTripList.length(); i++){
                    this.proposedList.add(inputTripList.getJSONObject(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else {
            tripList = new ArrayList<>();
            try {
                inputTripList = response.getJSONArray("trips");
                for (int i = 0; i < inputTripList.length(); i++){
                    if(!inputTripList.getJSONObject(i).getBoolean("isFulfilled")){
                        this.tripList.add(new Trip(inputTripList.getJSONObject(i)));
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
