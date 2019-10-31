package com.tagalong.tagalong;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class MapsFragment extends FragmentActivity implements OnMapReadyCallback
        , LocationListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener {
    private GoogleMap mMap;
    private FusedLocationProviderClient client;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentLocationMarker;
    public static final int PERMISSION_REQUEST_LOCATION_CODE = 101;
    double latitude, longitude;
    double end_latitude, end_longitude;
    private Profile userProfile;
    Button searchRoute;
    private Context context;
    private TextInputEditText arrivalDate;
    private TextInputEditText arrivalTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map);
        userProfile = (Profile) getIntent().getSerializableExtra("profile") ;
        context = getApplicationContext();
        client = LocationServices.getFusedLocationProviderClient(this);

        fetchLastLocation();

        searchRoute = (Button) findViewById(R.id.To);
        arrivalTime = (TextInputEditText) findViewById(R.id.arrivalTime);
        arrivalDate = (TextInputEditText) findViewById(R.id.arrivalDate);

        searchRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean allChecked = true;
                if (arrivalDate.getResources().toString().isEmpty()) {
                    Toast.makeText(context, "Please enter arrival date", Toast.LENGTH_LONG).show();
                    allChecked = false;
                }
                /*
                else {
                    if (!arrivalDate.getResources().toString().matches("^(3[0-1]|[1-2][0-9])/(1[0-2]"
                            + "|0[1-9]|0[1-9])/[0-9]{4}$")) {
                        Toast.makeText(context, "Please enter arrival date in the specified format", Toast.LENGTH_LONG).show();
                        allChecked = false;
                    }
                } */
                if (arrivalTime.getResources().toString().isEmpty()){
                    Toast.makeText(context, "Please enter arrival time", Toast.LENGTH_LONG).show();
                    allChecked = false;
                }
                /*
                else {
                    if (!arrivalTime.getResources().toString().matches("^(2[0-3]|[01][0-9]): (5[0-9])$")) {
                        Toast.makeText(context, "Please enter arrival time in the specified format", Toast.LENGTH_LONG).show();
                        allChecked = false;
                    }

                } */

                if (allChecked){
                    Trip trip = new Trip();
                    Object dataTransfer[] = new Object[3];
                    String url = getDirectionsURL();
                    GetDirectionsData getDirectionsData = new GetDirectionsData();
                    LatLng origin = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                    LatLng destination = new LatLng(end_latitude, end_longitude);
                    Calendar cal = Calendar.getInstance(); // creates calendar
                    cal.add(Calendar.HOUR_OF_DAY, 1); // adds one hour

                    dataTransfer[0] = mMap;
                    dataTransfer[1] = url;
                    dataTransfer[2] = destination;
                    getDirectionsData.execute(dataTransfer);

                    trip.setTripRoute(origin, destination);
                    trip.setUsername(userProfile.getUserName());
                    trip.setUserID(userProfile.getUserID());
                    trip.setDriverTrip(userProfile.getDriver());
                    trip.setArrivaltime(cal.getTime());
                    generateTrip(trip);
                }

            }
        });
    }

    private void fetchLastLocation() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]
                    {android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION_CODE);
            return;
        }

        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    lastLocation = location;
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(MapsFragment.this);
                }
            }
        });
    }

    private void generateTrip(Trip trip){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://ec2-50-17-82-63.compute-1.amazonaws.com/trips/newTrip";
        Gson gson = new Gson();
        String tripJson = gson.toJson(trip);
        JSONObject tripJSONObject;

        System.out.println(tripJson);

        try {
            tripJSONObject = new JSONObject(tripJson);



            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, tripJSONObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(context, "Successfully set trip", Toast.LENGTH_LONG).show();
                    //Intent intent = new Intent(MapsFragment.this, HomeActivity.class);
                    //startActivity(intent);
                    //MapsFragment.this.finish();
                    /*mMap.clear();
                    Object dataTransfer[] = new Object[3];
                    GetDirectionsData getDirectionsData = new GetDirectionsData();
                    String url = "";
                    dataTransfer[0] = mMap;
                    dataTransfer[1] = url;
                    dataTransfer[2] = new LatLng(end_latitude, end_longitude);
                    getDirectionsData.execute(dataTransfer);
*/
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println(error.toString());
                    Toast.makeText(context, "Please try again", Toast.LENGTH_LONG).show();
                }
            });

            queue.add(jsonObjectRequest);



        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_REQUEST_LOCATION_CODE :
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    fetchLastLocation();

                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        mMap.setMyLocationEnabled(true);
                    }


                    break;
                }
                else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
                return;
        }
    }




    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Current Location");


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mMap.setMyLocationEnabled(true);
        }


        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
        mMap.addMarker(markerOptions);
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMarkerClickListener(this);
        System.out.println(latitude);
        System.out.println(longitude);
        System.out.println(end_latitude);
        System.out.println(end_longitude);
    }


    private String getDirectionsURL(){
        StringBuilder googleDirectionsURL = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionsURL.append("origin="+latitude+","+longitude);
        googleDirectionsURL.append("&destination="+end_latitude+","+end_longitude);
        googleDirectionsURL.append("&key="+"AIzaSyDkjse1zwmX7lw71D5wpKIP0xrbKLG1YIQ");

        return googleDirectionsURL.toString();
    }

/*
    private String getDirectionsURL(double OriginLat, double originLng, double destLat, double destLng){
        StringBuilder googleDirectionsURL = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionsURL.append("origin="+OriginLat+","+originLng);
        googleDirectionsURL.append("&destination="+destLat+","+destLat);
        googleDirectionsURL.append("&key="+"AIzaSyDkjse1zwmX7lw71D5wpKIP0xrbKLG1YIQ");

        return googleDirectionsURL.toString();
    }

 */


    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        lastLocation = location;

        if (currentLocationMarker != null){
            currentLocationMarker.remove();
        }

        LatLng latLng = new  LatLng(location.getLatitude(),location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

        currentLocationMarker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public boolean checkLocationPermission(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION_CODE);
            }
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION_CODE);
            }
            return false;
        }
        else {
            return true;
        }
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.setDraggable(true);
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        end_latitude = marker.getPosition().latitude;
        end_longitude = marker.getPosition().longitude;
        System.out.println(latitude);
        System.out.println(longitude);
        System.out.println(end_latitude);
        System.out.println(end_longitude);
    }
}
