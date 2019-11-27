package com.tagalong.tagalong.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tagalong.tagalong.TripListTimmingLogger;
import com.tagalong.tagalong.adapter.TripViewAdapter;
import com.tagalong.tagalong.FirebaseMessagingServiceHandler;
import com.tagalong.tagalong.models.Profile;
import com.tagalong.tagalong.models.Trip;
import com.tagalong.tagalong.R;
import com.tagalong.tagalong.communication.VolleyCallback;
import com.tagalong.tagalong.communication.VolleyCommunicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyTripFragment extends Fragment {
    private final String TAG = "MyTripsFragment";
    private List<Trip> tripList;
    private View view;
    private Context context;
    private Profile profile;

    private TripListTimmingLogger tripListTimmingLogger;
    private BroadcastReceiver receiver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_trips, container, false);
        context = getActivity();
        Bundle inputBundle = getArguments();
        profile = (Profile) inputBundle.getSerializable("profile");
        tripListTimmingLogger = TripListTimmingLogger.getInstance();
        tripListTimmingLogger.reset();
        initTripList();
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                initTripList();
            }
        };

        // Re-initiate the the list of trips based on broadcast received on notification
        LocalBroadcastManager.getInstance(context).registerReceiver((receiver),
                new IntentFilter(FirebaseMessagingServiceHandler.REQUEST_ACCEPT)
        );
        return view;
    }

    /**
     * Get request to get list of trips to show
     */
    private void initTripList(){
        String url = getString(R.string.getTripList);
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("userID",profile.getUserID());

        VolleyCommunicator communicator = VolleyCommunicator.getInstance(context.getApplicationContext());
        VolleyCallback callback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response){
                Log.d(TAG, "Received list of trips for the user");
                tripListTimmingLogger.addSplit("Method: initTripList() - Successful get request for list of trips");
                setTripList(response);
                initTripView();
            }

            @Override
            public void onError(String result){
                tripListTimmingLogger.addSplit("Method: initTripList() - Failure get request for list of trips");
                Log.d(TAG, "Error: Could not get list of trips");
                Log.d(TAG, "Error: " + result);
                Toast.makeText(context, "We encountered some error,\nPlease reload the page", Toast.LENGTH_LONG).show();
            }
        };

        tripListTimmingLogger.addSplit("Method: initTripList() - get request for list of trips");
        communicator.volleyGet(url,callback,headers);
    }

    /**
     * Start the tripViewAdapter to load the my trip view.
     */
    private void initTripView(){
        Log.d(TAG,"start loading TripView");
        tripListTimmingLogger.addSplit("Method: initTripView() - Initiating recycler view to load of trip view");
        tripListTimmingLogger.dumpToLog();
        RecyclerView recyclerView = view.findViewById(R.id.my_trips_recycler_view);
        TripViewAdapter tripViewAdapter = new TripViewAdapter(context, this.tripList, profile);
        recyclerView.setAdapter(tripViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

    }

    /**
     * Set list of trips received from Get call
     * @param response response from get call
     */
    private void setTripList (JSONObject response){
        JSONArray inputTripList;
        tripList = new ArrayList<>();
        tripListTimmingLogger.addSplit("Method: setTripList() - begin to set local list of trips");
        try {
            inputTripList = response.getJSONArray("trips");
            for (int i = 0; i < inputTripList.length(); i++){
                if(inputTripList.getJSONObject(i).getBoolean("isFulfilled")){
                    this.tripList.add(new Trip(inputTripList.getJSONObject(i)));
                }
            }
            tripListTimmingLogger.addSplit("Method: setTripList() - done setting local list of trips");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(context).registerReceiver((receiver),
                new IntentFilter(FirebaseMessagingServiceHandler.REQUEST_ACCEPT));
    }
}