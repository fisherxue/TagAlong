package com.tagalong.tagalong;

import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.io.IOException;

public class GetDirectionsData extends AsyncTask<Object, String, String> {
    private GoogleMap mMap;
    private LatLng latLng;

    @Override
    protected String doInBackground(Object... objects) {
        String url;
        String googleDirectionsData = "";
        mMap = (GoogleMap)objects[0];
        url = (String) objects[1];
        latLng = (LatLng) objects[2];

        DownloadURL downloadURL = new DownloadURL();
        try {
            googleDirectionsData = downloadURL.readURL(url);

        } catch (IOException e){
            e.printStackTrace();
        }

        return googleDirectionsData;
    }

    @Override
    protected void onPostExecute(String s){
        String[] directionsList;
        DataParser parser = new DataParser();
        directionsList = parser.parseDirections(s);

        mMap.clear();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.draggable(true);
        markerOptions.title("Destination");

        if(directionsList != null) {
            displayDirections(directionsList);
        }

        mMap.addMarker(markerOptions);
    }

    public void displayDirections(String[] directionsList){
        int count = directionsList.length;
        for (int i = 0; i < count; i++){
            PolylineOptions options = new PolylineOptions();
            options.color(Color.BLUE);
            options.width(10);
            options.addAll(PolyUtil.decode(directionsList[i]));

            mMap.addPolyline(options);
        }
    }

}
