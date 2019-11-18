package com.tagalong.tagalong;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.datatype.Duration;

public class Trip implements Serializable {

    private String tripID;
    private String username;
    private boolean isDriverTrip;
    private JSONObject tripRoute;
    private Date arrivalTime;
    private Date departureTime;
    private String departurePlace;
    private String arrivalPlace;
    private String userID;
    private String[] taggedUsers;
    private String roomID;

    public Trip(){
       tripID = "not assigned";
       username = "not assigned";
       isDriverTrip = false;
       tripRoute = null;
       arrivalTime = new Date();
       departureTime = new Date();
       departurePlace = "not assigned";
       arrivalPlace = "not assigned";
       userID = "not assigned";
       roomID = "not assigned";
       taggedUsers = new String[]{"not assigned"};
    }

    public Trip(JSONObject trip) {
        try {
            this.username = trip.getString("username");
            this.tripID = trip.getString("_id");
            //this.roomID = trip.getString("roomID");
            this.tripRoute = trip.getJSONObject("tripRoute");
            //this.arrivalTime = (Date) trip.get("arrivalTime");
            this.arrivalTime = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'")
                    .parse(trip.getString("arrivalTime"));

            Long duration = this.tripRoute.getJSONArray("routes").getJSONObject(0)
                    .getJSONArray("legs").getJSONObject(0).getJSONObject("duration")
                    .getLong("value");
            this.departureTime = (Date) arrivalTime.clone();
            this.departureTime.setTime(this.arrivalTime.getTime()-(duration*1000));
            this.departurePlace = this.tripRoute.getJSONArray("routes").getJSONObject(0)
                    .getJSONArray("legs").getJSONObject(0).getString("start_address");
            this.arrivalPlace = this.tripRoute.getJSONArray("routes").getJSONObject(0)
                    .getJSONArray("legs").getJSONObject(0).getString("end_address");
            this.userID = trip.getString("userID");
            this.isDriverTrip = trip.getBoolean("isDriverTrip");
            JSONArray taggedUsers = trip.getJSONArray("taggedUsers");
            this.taggedUsers = new String[taggedUsers.length()];
            for (int i = 0; i < taggedUsers.length(); i++){
                this.taggedUsers[i] = taggedUsers.getString(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setTripRoute(LatLng origin, LatLng destination) {
        tripRoute = new JSONObject();

        try {
            tripRoute.put("origin", origin.latitude+","+origin.longitude);
            tripRoute.put("destination", destination.latitude+","+destination.longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    // Getters and Setters
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

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTripID() {
        return tripID;
    }

    public Date getDepartureTime() {
        return departureTime;
    }

    public String getDeparturePlace() {
        return departurePlace;
    }

    public String getArrivalPlace() {
        return arrivalPlace;
    }

    public String[] getTaggedUsers() {
        return taggedUsers;
    }

    public String getRoomID() {
        return roomID;
    }
}
