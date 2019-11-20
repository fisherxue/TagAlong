package com.tagalong.tagalong;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.SearchView;
import android.widget.TimePicker;
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

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MapsFragment extends FragmentActivity implements OnMapReadyCallback
        , LocationListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener {
    private GoogleMap mMap;
    private FusedLocationProviderClient client;
    private Location lastLocation;
    private Marker currentLocationMarker;
    public static final int PERMISSION_REQUEST_LOCATION_CODE = 101;
    private double latitude;
    private double longitude;
    private double end_latitude;
    private double end_longitude;
    private Profile userProfile;
    private Context context;
    private TextInputEditText arrivalDate;
    private TextInputEditText arrivalTime;
    private SearchView locationSearch;
    //private PlacesClient placesClient;
    private final String TAG = "MapFragment";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button searchRoute;
        setContentView(R.layout.fragment_map);
        userProfile = (Profile) getIntent().getSerializableExtra("profile") ;
        context = getApplicationContext();
        client = LocationServices.getFusedLocationProviderClient(this);

        fetchLastLocation();

        searchRoute = (Button) findViewById(R.id.To);
        arrivalTime = (TextInputEditText) findViewById(R.id.arrivalTime);
        arrivalDate = (TextInputEditText) findViewById(R.id.arrivalDate);
        locationSearch = (SearchView) findViewById(R.id.search);

        initializeSearch();
        initializeArrivalButtons();

        searchRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check and assign all inputs of user to profile
                boolean allChecked = true;
                if (arrivalDate.getResources().toString().isEmpty()) {
                    Toast.makeText(context, "Please enter arrival date", Toast.LENGTH_LONG).show();
                    allChecked = false;
                }
                else {
                    if (!arrivalDate.getText().toString().replaceAll("\\s+","")
                            .matches("^(3[0-1]|[0-2][0-9])/(1[0-2]|0[1-9]|0[1-9])/[0-9]{4}$")) {
                        Toast.makeText(context, "Please enter arrival date in the specified format", Toast.LENGTH_LONG).show();
                        allChecked = false;
                    }
                }
                if (arrivalTime.getText().toString().isEmpty()){
                    Toast.makeText(context, "Please enter arrival time", Toast.LENGTH_LONG).show();
                    allChecked = false;
                }
                else {
                    if (!arrivalTime.getText().toString().replaceAll("\\s+","")
                            .matches("^(2[0-3]|[0-1][0-9]):([0-5][0-9])$")) {
                        Toast.makeText(context, "Please enter arrival time in the specified format", Toast.LENGTH_LONG).show();
                        allChecked = false;
                    }
                }

                if (allChecked){
                    Trip trip = new Trip();
                    Object dataTransfer[] = new Object[3];
                    String url = getDirectionsURL();
                    GetDirectionsData getDirectionsData = new GetDirectionsData();
                    LatLng origin = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
                    LatLng destination = new LatLng(end_latitude, end_longitude);

                    dataTransfer[0] = mMap;
                    dataTransfer[1] = url;
                    dataTransfer[2] = destination;
                    getDirectionsData.execute(dataTransfer);

                    String dateTimeString = arrivalDate.getText().toString() + " " +
                            arrivalTime.getText().toString();

                    Date arrivalDate = new Date();
                    try {
                        arrivalDate = new SimpleDateFormat("dd/MM/yyyy HH:mm")
                                .parse(dateTimeString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    trip.setTripRoute(origin, destination);
                    trip.setUsername(userProfile.getUserName());
                    trip.setUserID(userProfile.getUserID());
                    trip.setDriverTrip(userProfile.getDriver());
                    trip.setArrivalTime(arrivalDate);
                    generateTrip(trip);
                }

            }
        });
    }




    private void initializeSearch() {

        //Parses string in search and finds location.
        locationSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = locationSearch.getQuery().toString();
                List<Address> locationList = new ArrayList<>();
                Geocoder geocoder = new Geocoder(MapsFragment.this);
                try {
                    locationList = geocoder.getFromLocationName(location, 1);
                    Log.d(TAG,"Searching for Location");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mMap.clear();
                if(!locationList.isEmpty()) {
                    Log.d(TAG,"Location was found.");
                    Address address = locationList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    end_latitude = latLng.latitude;
                    end_longitude = latLng.longitude;
                }
                else {
                    Toast.makeText(context, "Location not Found", Toast.LENGTH_LONG).show();
                    Log.d(TAG,"Location was not found");
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


    }


    private void initializeArrivalButtons () {
        arrivalDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();

                DatePickerDialog datePickerDialog = new DatePickerDialog(MapsFragment.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                Calendar calendarOneYearAdvance = (Calendar)calendar.clone();
                                Calendar calendarOneDayAdvance = (Calendar)calendar.clone();
                                calendarOneYearAdvance.add(Calendar.YEAR,1);
                                calendarOneDayAdvance.add(Calendar.DAY_OF_MONTH,1);
                                Calendar calendarSetDate = Calendar.getInstance();
                                calendarSetDate.set(year,month,day);
                                //calendarSetDate.add(Calendar.DAY_OF_MONTH, 1);
                                if (calendarSetDate.after(calendarOneDayAdvance) && calendarSetDate.before(calendarOneYearAdvance)){
                                    arrivalDate.setText(String.format("%02d/%02d/%04d",day,month+1,year));
                                    Log.d(TAG,"Arrival date was correctly filled.");
                                }
                                else {
                                    Toast.makeText(context, "Please enter date that is within one year from today", Toast.LENGTH_LONG).show();
                                    Log.d(TAG,"Not appropriate arrival date.");
                                }
                            }
                        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        arrivalTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(MapsFragment.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        arrivalTime.setText(String.format("%02d:%02d",hour,minute));
                        Log.d(TAG,"Arrival time was set.");
                    }
                },0,0,false);
                timePickerDialog.show();
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
                    Log.d(TAG,"Current Location set.");
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
            Log.d(TAG,tripJSONObject.toString());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, tripJSONObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(context, "Successfully set trip", Toast.LENGTH_LONG).show();
                    Log.d(TAG,"Trip was successfully set.");
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
                    Log.d(TAG, "Error: while sending trip");
                    Log.d(TAG, "Error: " + error.toString());
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
                        Log.d(TAG,"Location enabled.");
                        mMap.setMyLocationEnabled(true);
                    }

                    break;
                }
                else {
                    Log.d(TAG,"Location not enabled");
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
                return;
            default:
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
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
        mMap.addMarker(markerOptions);
        mMap.setOnMarkerDragListener(this);
        mMap.setOnMarkerClickListener(this);

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
        //Nothing to be done
    }

    @Override
    public void onProviderEnabled(String provider) {
        //Nothing to be done
    }

    @Override
    public void onProviderDisabled(String provider) {
        //Nothing to be done
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
        //Nothing to be done
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        //Nothing to be done
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

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra("profile", userProfile);
        startActivity(intent);
        MapsFragment.this.finish();
    }
}
