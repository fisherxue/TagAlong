package com.tagalong.tagalong;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import java.util.Map;

public class VolleyCommunicator {
    private static VolleyCommunicator volleyCommunicator;
    private RequestQueue requestQueue;
    private static Context context;

    private VolleyCommunicator(Context context){
        this.context = context;
        if (requestQueue == null){
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
    }

    public static synchronized VolleyCommunicator getInstance(Context context){
        if (volleyCommunicator == null){
            volleyCommunicator = new VolleyCommunicator(context);
        }
        return volleyCommunicator;
    }

    public void VolleyGet(String url, final VolleyCallback volleyCallback, final Map<String, String> headers) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                volleyCallback.onSuccess(response);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyCallback.onError(error.toString());
            }
        }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    public void VolleyPost(String url, JSONObject jsonObject, final VolleyCallback volleyCallback) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                volleyCallback.onSuccess(response);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                volleyCallback.onError(error.toString());
            }
        }
        );
        requestQueue.add(jsonObjectRequest);
    }
}

