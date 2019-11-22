package com.tagalong.tagalong.Activity;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.tagalong.tagalong.DataParser;
import com.tagalong.tagalong.R;

import org.json.JSONException;
import org.json.JSONObject;

public class TripDisplayActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String trip;
    LatLng origin;
    LatLng destination;
    private final String TAG = "TripDisplayActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips_display);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Intent intent = getIntent();
        trip = intent.getStringExtra("tripRoute");
        setOriginAndDest();
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        String[] directionsList;
        DataParser parser = new DataParser();
        directionsList = parser.parseDirections(trip);

        mMap.clear();
        MarkerOptions originMarker = new MarkerOptions();
        MarkerOptions destinationMarker = new MarkerOptions();
        originMarker.position(origin);
        originMarker.title("Origin");
        destinationMarker.position(destination);
        destinationMarker.title("Destination");

        if(directionsList != null) {
            displayDirections(directionsList);
        }

        mMap.addMarker(originMarker);
        mMap.addMarker(destinationMarker);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 12));
    }

    private void setOriginAndDest() {
        JSONObject jsonTrip = null;
        double originLat;
        double originLng;
        double destLat;
        double destLng;

        try {
            jsonTrip = new JSONObject(trip);
            originLat = jsonTrip.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("start_location").getDouble("lat");
            originLng = jsonTrip.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("start_location").getDouble("lng");
            destLat = jsonTrip.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("end_location").getDouble("lat");
            destLng = jsonTrip.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("end_location").getDouble("lng");
            origin = new LatLng(originLat, originLng);
            destination = new LatLng(destLat, destLng);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void displayDirections(String[] directionsList){
        int count = directionsList.length;
        for (int i = 0; i < count; i++){
            PolylineOptions options = new PolylineOptions();
            options.color(Color.RED);
            options.width(10);
            options.addAll(PolyUtil.decode(directionsList[i]));
            Log.d(TAG,"Trip is displayed.");
            mMap.addPolyline(options);
        }
    }

}
