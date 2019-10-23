package com.example.tagalong;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

public class Trip implements Serializable {

    private String username;
    private boolean isDriverTrip;
    private JSONObject tripRoute;
    private Date arrivaltime;

    public void setTripRoute(LatLng origin, LatLng destination) {
        tripRoute = new JSONObject();

        try {
            tripRoute.put("origin", origin.latitude+","+origin.longitude);
            tripRoute.put("destination", destination.latitude+","+destination.longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public JSONObject getTripRoute() {
        return tripRoute;
    }

    public void setUsername(String userName) {
        this.username = userName;
    }

    public String getUsername() {
        return username;
    }

    public void setDriverTrip(boolean isDriverTrip) {
        this.isDriverTrip = isDriverTrip;
    }

    public boolean isDriverTrip() {
        return isDriverTrip;
    }

    public void setArrivaltime(Date arrivaltime) {
        this.arrivaltime = arrivaltime;
    }

    public Date getArrivaltime() {
        return arrivaltime;
    }
}
