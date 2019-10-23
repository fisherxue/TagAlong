package com.example.tagalong;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

public class Signup2_Fragment extends Fragment {
    private EditText age,gen,interest,email,carcap;
    private Button submit;
    private Switch isDriver;
    private Context context;
    private boolean allSet;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_2, container, false);
        final Bundle bundle = getArguments();
        context = getActivity();
        final Profile newUserProfile = (Profile) bundle.getSerializable("profile");

        age = (EditText) view.findViewById(R.id.age);
        gen = (EditText) view.findViewById(R.id.gender);
        interest = (EditText) view.findViewById(R.id.intrests);
        email = (EditText) view.findViewById(R.id.email);
        carcap = (EditText) view.findViewById(R.id.carCapacity);
        isDriver = (Switch) view.findViewById(R.id.isDriver);
        submit = (Button) view.findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allSet = true;

                if (!age.getText().toString().isEmpty()){
                    newUserProfile.setAge(Integer.parseInt(age.getText().toString()));
                } else {
                    newUserProfile.setAge(0);
                    Toast.makeText(context, "Please Enter Age", Toast.LENGTH_LONG).show();
                    allSet = false;
                }

                if (!email.getText().toString().isEmpty()){
                    newUserProfile.setEmail(email.getText().toString());
                } else {
                    Toast.makeText(context, "Please Enter Email Address", Toast.LENGTH_LONG).show();
                    allSet = false;
                }

                if (!gen.getText().toString().isEmpty()){
                    newUserProfile.setGender(gen.getText().toString());
                } else {
                    Toast.makeText(context, "Please Enter Gender", Toast.LENGTH_LONG).show();
                    allSet = false;
                }

                if (!interest.getText().toString().isEmpty()){
                    newUserProfile.setInterest(interest.getText().toString());
                } else {
                    Toast.makeText(context, "Please Enter Interests", Toast.LENGTH_LONG).show();
                    allSet = false;
                }


                newUserProfile.setDriver(false);
                newUserProfile.setCarCapacity(0);
                if(isDriver.isChecked()){
                    newUserProfile.setDriver(true);
                    if (!carcap.getText().toString().isEmpty()){
                        newUserProfile.setCarCapacity(Integer.parseInt(carcap.getText().toString()));
                    } else {
                        Toast.makeText(context, "Please Enter Car Capacity", Toast.LENGTH_LONG).show();
                        allSet = false;
                    }
                }

                if (allSet) {
                    sendProfile(newUserProfile);
                }

            }
        });
        return view;
    }

    void sendProfile(Profile profile) {

        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "http://206.87.96.130:3000/users/register";
        Gson gson = new Gson();
        String profileJson = gson.toJson(profile);
        JSONObject profileJsonObject;

        System.out.println(profileJson.toString());

        try {
            profileJsonObject = new JSONObject((profileJson));

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, profileJsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(context, "Successfully signed up", Toast.LENGTH_LONG).show();
                    final Profile recieved_profile = new Profile();
                    try {
                        recieved_profile.setUserName(response.getString("username"));
                        recieved_profile.setInterest(response.getString("interests"));
                        recieved_profile.setFirstName(response.getString("firstName"));
                        recieved_profile.setLastName(response.getString("lastName"));
                        recieved_profile.setAge(response.getInt("age"));
                        recieved_profile.setGender(response.getString("gender"));
                        recieved_profile.setEmail(response.getString("email"));
                        recieved_profile.setPassword(response.getString("password"));
                        recieved_profile.setDriver(response.getBoolean("isDriver"));
                        recieved_profile.set_id(response.getString("_id"));
                        recieved_profile.setJoinedDate(response.getString("joinedDate"));
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    intent.putExtra("profile", recieved_profile);
                    startActivity(intent);
                    getActivity().finish();

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

        /*
    // Request a string response from the provided URL.
        StringRequest postRequest = new StringRequest(Request.POST.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        textView.setText("Response is: "+ response.substring(0,20));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textView.setText("That didn't work!");
            }
        });

    // Add the request to the RequestQueue.
        queue.add(stringRequest); */
    //}
}

