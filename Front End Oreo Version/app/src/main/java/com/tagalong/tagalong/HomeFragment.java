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

public class HomeFragment extends Fragment {
    private final String TAG = "HomeFragment";
    private List<Trip> tripList;
    private View view;
    private Context context;
    private Profile profile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        context = getActivity();
        Bundle inputBundle = getArguments();
        profile = (Profile) inputBundle.getSerializable("profile");
        initTripList();
        return view;
    }

    private void initTripList(){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = getString(R.string.getTripList);
        final Gson gson = new Gson();
        final String profileJson = gson.toJson(profile);
        Log.d(TAG, "profileJson" + profileJson);
        JSONObject profileJsonObject;

        try {
            profileJsonObject = new JSONObject((profileJson));
            Log.d(TAG, "profileJsonObject" + profileJsonObject);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, profileJsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "Received List of Trips for the user");
                    setTripList(response);
                    initTripView();
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
    private void initTripView(){
        Log.d(TAG,"initializing TripView");
        RecyclerView recyclerView = view.findViewById(R.id.home_frag_recycler_view);
        TripViewAdapter tripViewAdapter = new TripViewAdapter(context, this.tripList, profile);
        recyclerView.setAdapter(tripViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    private void setTripList (JSONObject response){
        JSONArray tripListIN;
        tripList = new ArrayList<>();
        try {
            tripListIN = response.getJSONArray("trips"); // ASK IAN FOR CORRECT NAME
            Log.d(TAG, "Trip Array: " + tripListIN.toString());
            for (int i = 0; i < tripListIN.length(); i++){
                this.tripList.add(new Trip(tripListIN.getJSONObject(i)));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
