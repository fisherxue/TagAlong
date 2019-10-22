package com.example.tagalong;

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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONObject;

public class Signup2_Fragment extends Fragment {
    private EditText age,gen,interest,email,carcap;
    private Button submit;
    private Switch isDriver;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_2, container, false);
        final Bundle bundle = getArguments();

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
                newUserProfile.setAge(0);
                if (!age.getText().toString().equals("")){
                    newUserProfile.setAge(Integer.parseInt(age.getText().toString()));
                }
                newUserProfile.setEmail(email.getText().toString());
                newUserProfile.setGender(gen.getText().toString());
                newUserProfile.setInterest(interest.getText().toString());
                newUserProfile.setDriver(false);
                newUserProfile.setCarCapacity(0);
                if(isDriver.isChecked()){
                    newUserProfile.setDriver(true);
                    if (!carcap.getText().toString().equals("")){
                        newUserProfile.setCarCapacity(Integer.parseInt(carcap.getText().toString()));
                    }
                }

                //sendProfile(newUserProfile);


                Intent intent = new Intent(getActivity(), HomeActivity.class);
                intent.putExtra("profile", newUserProfile);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return view;
    }
    /*
    void sendProfile(Profile profile){

        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://ec2-50-17-82-63.compute-1.amazonaws.com/";

        Gson gson = new Gson();
        String profileJson = gson.toJson(profile);

    */
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

