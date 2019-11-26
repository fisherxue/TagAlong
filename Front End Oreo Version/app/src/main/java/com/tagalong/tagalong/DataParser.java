package com.tagalong.tagalong;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class DataParser {

    public String[] parseDirections(String jsonData){
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonData);
            for(int i = 0; i < jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").length(); i++) {
                if (i == 0) {
                    jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(i).getJSONArray("steps");
                } else {
                    for (int j = 0; j < jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(i).getJSONArray("steps").length(); j++) {
                        jsonArray.put(jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(i).getJSONArray("steps").getJSONObject(j));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (jsonArray != null) {
            return getPaths(jsonArray);
        }
        return null;
    }

    public String[] getPaths(JSONArray googleStepsJson){
        int count = googleStepsJson.length();
        String [] polylines = new String[count];

        for (int i = 0; i < count; i++){
            try {
                polylines[i] = getPath(googleStepsJson.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return polylines;
    }

    public String getPath(JSONObject googlePathJson){
        String polyline = "";
        try {
            polyline = googlePathJson.getJSONObject("polyline").getString("points");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return polyline;
    }
}
