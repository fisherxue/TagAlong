package com.tagalong.tagalong;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class Trip implements Serializable {

    private String username;
    private boolean isDriverTrip;
    private JSONObject tripRoute;
    private Date arrivaltime;
    private String userID;
    //private String[] taggedUsers;

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

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
