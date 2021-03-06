package com.tagalong.tagalong.fragment;

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
import com.tagalong.tagalong.NotificationTimmingLogger;
import com.tagalong.tagalong.activity.HomeActivity;
import com.tagalong.tagalong.GetDirectionsData;
import com.tagalong.tagalong.models.Profile;
import com.tagalong.tagalong.models.Trip;
import com.tagalong.tagalong.R;
import com.tagalong.tagalong.communication.VolleyCallback;
import com.tagalong.tagalong.communication.VolleyCommunicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Fragment for the user to create a new trip using Google
 * Maps API
 */

public class SetTripFragment extends FragmentActivity implements OnMapReadyCallback
        , LocationListener {
    private GoogleMap mMap;
    private FusedLocationProviderClient client;
    private Location lastLocation;
    private LatLng markerLocation;
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
    private Button setOrigin;
    private final String TAG = "MapFragment";
    private NotificationTimmingLogger notificationTimmingLogger;
    private MarkerOptions originMarker = null;


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
        setOrigin = (Button) findViewById(R.id.setOrigin);
        arrivalTime = (TextInputEditText) findViewById(R.id.arrivalTime);
        arrivalDate = (TextInputEditText) findViewById(R.id.arrivalDate);
        locationSearch = (SearchView) findViewById(R.id.search);
        notificationTimmingLogger = NotificationTimmingLogger.getInstance();
        initializeSearch();
        initializeOrigin();
        initializeArrivalButtons();

        searchRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //check and assign all inputs of user to profile
            notificationTimmingLogger.reset();
            notificationTimmingLogger.addSplit("Set Trip Fragment: Clicked on searchRoute");
            boolean allChecked = true;
            if (markerLocation == null) {
                Toast.makeText(context, "Please set the destination", Toast.LENGTH_LONG).show();
                return;
            }
            end_latitude = markerLocation.latitude;
            end_longitude = markerLocation.longitude;
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
                LatLng origin = new LatLng(latitude, longitude);
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
                trip.setUsername(userProfile.getUsername());
                trip.setUserID(userProfile.getUserID());
                trip.setDriverTrip(userProfile.getDriver());
                trip.setArrivalTime(arrivalDate);
                notificationTimmingLogger.addSplit("Set Trip Fragment: all checks before post pass");
                generateTrip(trip);
                }

            }
        });
    }

    /**
     * This function sets a location chosen by the user to be the
     * initial point of the trip
     *
     */
    private void initializeOrigin() {
        setOrigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(markerLocation != null) {
                    latitude = markerLocation.latitude;
                    longitude = markerLocation.longitude;

                    originMarker = new MarkerOptions();
                    originMarker.position(new LatLng(latitude, longitude));
                    originMarker.draggable(false);
                    originMarker.title("Origin");
                    mMap.clear();
                    mMap.addMarker(originMarker);
                    //mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)));

                    Toast.makeText(context, "Origin was set to chosen location", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Select a departure location", Toast.LENGTH_LONG).show();
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
            Geocoder geocoder = new Geocoder(SetTripFragment.this);
            try {
                locationList = geocoder.getFromLocationName(location, 1);
                Log.d(TAG,"Searching for Location");
            } catch (IOException e) {
                e.printStackTrace();
            }

            mMap.clear();
            if (originMarker != null) {
                mMap.addMarker(originMarker);
            }
            if(!locationList.isEmpty()) {
                Log.d(TAG,"Location was found.");
                Address address = locationList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                markerOptions.title("Destination");
                mMap.addMarker(markerOptions);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                markerLocation = latLng;
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


    /**
     * The function initializes the arrival buttons so that
     * the user can set the arrival time and date using a date and time picker
     */
    private void initializeArrivalButtons () {
        arrivalDate.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(SetTripFragment.this,
                new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar calendarOneYearAdvance = (Calendar)calendar.clone();
                Calendar calendarOneDayAdvance = (Calendar)calendar.clone();
                calendarOneYearAdvance.add(Calendar.YEAR,1);
                calendarOneDayAdvance.add(Calendar.DAY_OF_MONTH,1);
                Calendar calendarSetDate = Calendar.getInstance();
                calendarSetDate.set(year,month,day);
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(SetTripFragment.this, new TimePickerDialog.OnTimeSetListener() {
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

    /**
     * This function retrieves the current location of the user
     * in order for the user to be able to set a trip from the current location
     */
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
                    mapFragment.getMapAsync(SetTripFragment.this);
                }
            }
        });
    }

    /**
     * This function generates a new trip based on origin and destination chosen by the user.
     * It also sends the trip to the backend to process the trip
     * @param trip is the trip created by the user based on the origin and destination location
     */
    private void generateTrip(Trip trip){
        String url = getString(R.string.createTrip);
        Gson gson = new Gson();
        String tripJson = gson.toJson(trip);
        Log.d(TAG, tripJson);
        JSONObject tripJSONObject;
        VolleyCommunicator communicator = VolleyCommunicator.getInstance(context);
        VolleyCallback callback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                Toast.makeText(context, "Successfully set trip", Toast.LENGTH_LONG).show();
                Log.d(TAG,"Trip was successfully set.");
                notificationTimmingLogger.addSplit("Set Trip Fragment: Method generateTrip() - post the trip Success");
                Intent intent = new Intent(SetTripFragment.this, HomeActivity.class);
                intent.putExtra("profile", userProfile);
                startActivity(intent);
                SetTripFragment.this.finish();
            }

        @Override
        public void onError(String result) {
            Log.d(TAG, "Error: while sending trip");
            Log.d(TAG, "Error: " + result);
            notificationTimmingLogger.addSplit("Set Trip Fragment: Method generateTrip() - post the trip Error");
            Toast.makeText(context, "Please try again", Toast.LENGTH_LONG).show();
            }
        };

        notificationTimmingLogger.addSplit("Set Trip Fragment: Method generateTrip() - post the trip");
        try {
            tripJSONObject = new JSONObject((tripJson));
            communicator.volleyPost(url,tripJSONObject,callback);
        } catch (JSONException e) {
            Log.d(TAG, "Error making trip JSONObject");
            Log.d(TAG, "JSONException: " + e.toString());
            e.printStackTrace();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
        case PERMISSION_REQUEST_LOCATION_CODE :
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                fetchLastLocation();

                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED && mMap!=null){
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
   * If Google Play services is not installed on the device, the user will be prompted to install
   * it inside the SupportMapFragment. This method will only be triggered once the user has
   * installed Google Play services and returned to the app.
   */
    @Override
    public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;
    LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
    MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Current Location");
    latitude = lastLocation.getLatitude();
    longitude = lastLocation.getLongitude();


    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
        mMap.setMyLocationEnabled(true);
    }

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                MarkerOptions marker = new MarkerOptions();
                marker.position(latLng);
                marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                marker.title("Destination");
                mMap.clear();
                if (originMarker != null) {
                    mMap.addMarker(originMarker);
                }
                mMap.addMarker(marker);
                markerLocation = latLng;
            }
        });

        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
        mMap.addMarker(markerOptions);

    }


    private String getDirectionsURL(){
        StringBuilder googleDirectionsURL = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionsURL.append("origin="+latitude+","+longitude);
        googleDirectionsURL.append("&destination="+end_latitude+","+end_longitude);
        googleDirectionsURL.append("&key="+getString(R.string.google_directions_key));

        return googleDirectionsURL.toString();
    }

    @Override
    public void onLocationChanged(Location location) {
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra("profile", userProfile);
        startActivity(intent);
        SetTripFragment.this.finish();
    }
}