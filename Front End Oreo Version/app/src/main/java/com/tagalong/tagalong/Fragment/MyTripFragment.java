package com.tagalong.tagalong.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.TimingLogger;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tagalong.tagalong.Adapter.TripViewAdapter;
import com.tagalong.tagalong.Models.Profile;
import com.tagalong.tagalong.Models.Trip;
import com.tagalong.tagalong.R;
import com.tagalong.tagalong.Communication.VolleyCallback;
import com.tagalong.tagalong.Communication.VolleyCommunicator;

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
        view = inflater.inflate(R.layout.fragment_my_trips, container, false);
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
        JSONArray inputTripList;
        tripList = new ArrayList<>();
        timingLogger.addSplit("setTripList - creating tripList");
        try {
            inputTripList = response.getJSONArray("trips");
            for (int i = 0; i < inputTripList.length(); i++){
                if(inputTripList.getJSONObject(i).getBoolean("isFulfilled")){
                    this.tripList.add(new Trip(inputTripList.getJSONObject(i)));
                }
            }
            timingLogger.addSplit("setTripList - created tripList");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
