package com.tagalong.tagalong;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.TimingLogger;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyTripFragment extends Fragment {
    private final String TAG = "My Trips Fragment";
    private List<Trip> tripList;
    private View view;
    private Context context;
    private Profile profile;

    private TimingLogger timingLogger;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        context = getActivity();
        Bundle inputBundle = getArguments();
        profile = (Profile) inputBundle.getSerializable("profile");
        timingLogger = new TimingLogger(TAG, "My Trips Activity");
        initTripList();
        return view;
    }

    private void initTripList(){
        String url = getString(R.string.getTripList);
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("userID",profile.getUserID());

        VolleyCommunicator communicator = VolleyCommunicator.getInstance(context.getApplicationContext());
        VolleyCallback callback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response){
                Log.d(TAG, "Received list of trips for the user");
                timingLogger.addSplit("initTripList - Got list of trips");
                setTripList(response);
                initTripView();
            }

            @Override
            public void onError(String result){
                timingLogger.addSplit("initTripList - Received error");
                Log.d(TAG, "Error: Could not get list of trips");
                Log.d(TAG, "Error: " + result);
                Toast.makeText(context, "We encountered some error,\nPlease reload the page", Toast.LENGTH_LONG).show();
            }
        };

        timingLogger.addSplit("initTripList - Send Request to get list of trips");
        communicator.VolleyGet(url,callback,headers);
    }

    private void initTripView(){
        Log.d(TAG,"initializing TripView");
        timingLogger.addSplit("initTripView - Start Adapter");
        timingLogger.dumpToLog();
        timingLogger.reset();
        RecyclerView recyclerView = view.findViewById(R.id.my_trips_recycler_view);
        TripViewAdapter tripViewAdapter = new TripViewAdapter(context, this.tripList, profile);
        recyclerView.setAdapter(tripViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    private void setTripList (JSONObject response){
        JSONArray tripListIN;
        tripList = new ArrayList<>();
        timingLogger.addSplit("setTripList - creating tripList");
        try {
            tripListIN = response.getJSONArray("trips"); // ASK IAN FOR CORRECT NAME
            for (int i = 0; i < tripListIN.length(); i++){
                if(tripListIN.getJSONObject(i).getBoolean("isFulfilled")){
                    this.tripList.add(new Trip(tripListIN.getJSONObject(i)));
                }
            }
            timingLogger.addSplit("setTripList - created tripList");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
