package com.tagalong.tagalong;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UpdateProfileActivity extends AppCompatActivity {

    private String TAG = "Update Profile Activity";
    private EditText ageEditText;
    private EditText genderEditText;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText carCapacityEditText;
    private Button submitButton;
    private Switch isDriverSwitch;
    private SeekBar musicSeekBar;
    private SeekBar smokingSeekBar;
    private SeekBar speedSeekBar;
    private SeekBar fragranceSeekBar;
    private SeekBar chattingSeekBar;

    private Context context;
    private Profile userProfile;
    private int[] interests = {2,2,2,2,2};
    private Profile newUserProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        context = getApplicationContext();
        userProfile = (Profile) getIntent().getSerializableExtra("profile") ;

        Log.d(TAG,"Started Update Profile Activity");

        firstNameEditText = (EditText) findViewById(R.id.firstName);
        lastNameEditText = (EditText) findViewById(R.id.lastName);
        ageEditText = (EditText) findViewById(R.id.age);
        genderEditText = (EditText) findViewById(R.id.gender);
        carCapacityEditText = (EditText) findViewById(R.id.carCapacity);
        isDriverSwitch = (Switch) findViewById(R.id.isDriver);
        submitButton = (Button) findViewById(R.id.submit);
        musicSeekBar = (SeekBar) findViewById(R.id.seekMusic);
        smokingSeekBar = (SeekBar) findViewById(R.id.seekSmoking);
        fragranceSeekBar = (SeekBar) findViewById(R.id.seekFragrance);
        speedSeekBar = (SeekBar) findViewById(R.id.seekSpeed);
        chattingSeekBar = (SeekBar) findViewById(R.id.seekChatting);

        initializeSeekBar();
    }

    private void initializeSeekBar(){
        Log.d(TAG,"Initializing SeekBars");
        musicSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                interests[0] = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Nothing To be Done
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Nothing To be Done
            }
        });

        chattingSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                interests[1] = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Nothing To be Done
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Nothing To be Done
            }
        });

        speedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                interests[2] = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Nothing To be Done
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Nothing To be Done
            }
        });

        fragranceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                interests[3] = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Nothing To be Done
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Nothing To be Done
            }
        });

        smokingSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                interests[4] = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Nothing To be Done
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Nothing To be Done
            }
        });
        Log.d(TAG,"SeekBars Initialized");
    }

    @Override
    protected void onStart() {
        super.onStart();

        newUserProfile = new Profile();
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean allSet = true;

                if (!firstNameEditText.getText().toString().isEmpty()) {
                    Log.d(TAG,"First name set");
                    newUserProfile.setFirstName(firstNameEditText.getText().toString());
                }
                else {
                    Toast.makeText(context, "Please Enter First Name", Toast.LENGTH_LONG).show();
                    allSet = false;
                    Log.d(TAG,"First name not set");
                }

                if (!lastNameEditText.getText().toString().isEmpty()) {
                    newUserProfile.setLastName(lastNameEditText.getText().toString());
                    Log.d(TAG,"Last name set");
                }
                else {
                    Toast.makeText(context, "Please Enter Last Name", Toast.LENGTH_LONG).show();
                    allSet = false;
                    Log.d(TAG,"Last name not set");
                }

                if (!ageEditText.getText().toString().isEmpty()) {
                    allSet = verifyAge(allSet);
                    Log.d(TAG,"Age set");
                }
                else {
                    Toast.makeText(context, "Please Enter Age", Toast.LENGTH_LONG).show();
                    allSet = false;
                    Log.d(TAG,"Age not set");
                }

                if (!genderEditText.getText().toString().isEmpty()){
                    newUserProfile.setGender(genderEditText.getText().toString());
                    Log.d(TAG,"Gender set");
                } else {
                    Toast.makeText(context, "Please Enter Gender", Toast.LENGTH_LONG).show();
                    Log.d(TAG,"Gender not set");
                    allSet = false;
                }

                newUserProfile.setDriver(false);
                newUserProfile.setCarCapacity(0);
                if(isDriverSwitch.isChecked() && newUserProfile.getAge() >= newUserProfile.minAgeDriver){
                    newUserProfile.setDriver(true);
                    if (!carCapacityEditText.getText().toString().isEmpty()){
                        newUserProfile.setCarCapacity(Integer.parseInt(carCapacityEditText.getText().toString()));
                        Log.d(TAG,"Car capacity and Driver set");
                    } else {
                        Toast.makeText(context, "Please Enter Car Capacity", Toast.LENGTH_LONG).show();
                        Log.d(TAG,"Car capacity not set");
                        allSet = false;
                    }
                }
                else if (isDriverSwitch.isChecked() && newUserProfile.getAge() < newUserProfile.minAgeDriver) {
                    Toast.makeText(context, "You are underage to be a driver \n Please register as a rider", Toast.LENGTH_LONG).show();
                    allSet = false;
                    Log.d(TAG,"Age Requirements not met to be driver");
                }

                newUserProfile.setInterests(interests);
                newUserProfile.setUserID(userProfile.getUserID());
                newUserProfile.setEmail(userProfile.getEmail());
                newUserProfile.setUserName(userProfile.getUserName());
                newUserProfile.setPassword(userProfile.getPassword());
                newUserProfile.setJoinedDate(userProfile.getJoinedDate());

                if (allSet) {
                    sendProfile(newUserProfile);
                }

            }
        });
    }

    private boolean verifyAge(boolean allSetIn){
        Log.d(TAG,"verifying user age");
        boolean allSet = allSetIn;
        if (Integer.parseInt(ageEditText.getText().toString()) < newUserProfile.minAgeRider) {
            Toast.makeText(context, "You are underage to register", Toast.LENGTH_LONG).show();
            allSet = false;
        }
        else if (Integer.parseInt(ageEditText.getText().toString()) > newUserProfile.maxAge) {
            Toast.makeText(context, "Please Enter Valid Age", Toast.LENGTH_LONG).show();
            allSet = false;
        }
        else {
            newUserProfile.setAge(Integer.parseInt(ageEditText.getText().toString()));
        }
        return  allSet;
    }

    private void sendProfile(Profile profile) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = getString(R.string.updateProfile);

        Gson gson = new Gson();
        String profileJson = gson.toJson(profile);
        JSONObject profileJsonObject;
        try {
            Log.d(TAG,"Sending user profile information");
            profileJsonObject = new JSONObject((profileJson));
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, profileJsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(context, "Update Profile Successful", Toast.LENGTH_LONG).show();
                    Log.d(TAG,"Successfully updated user profile");
                    final Profile received_profile = new Profile();
                    try {
                        received_profile.setUserName(response.getString("username"));
                        JSONArray jsonArray = response.getJSONArray("interests");
                        int [] interests = new int[jsonArray.length()];
                        for (int i = 0; i < jsonArray.length(); i++){
                            interests[i] = jsonArray.getInt(i);
                        }
                        received_profile.setInterests(interests);
                        received_profile.setAge(response.getInt("age"));
                        received_profile.setGender(response.getString( "gender"));
                        received_profile.setEmail(response.getString("email"));
                        received_profile.setPassword(response.getString("password"));
                        received_profile.setDriver(response.getBoolean("isDriver"));
                        received_profile.setUserID(response.getString("_id"));
                        received_profile.setJoinedDate(response.getString("joinedDate"));
                        received_profile.setFirstName(response.getString("firstName"));
                        received_profile.setLastName(response.getString("lastName"));
                    } catch (JSONException e) {
                        Log.d(TAG,"Error while retrieving profile");
                        Log.d(TAG,"Error" + e.toString());
                    }

                    Log.d(TAG,"Successfully Retrieved profile");
                    Intent intent = new Intent(UpdateProfileActivity.this, HomeActivity.class);
                    intent.putExtra("profile", received_profile);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    UpdateProfileActivity.this.finish();
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG,"Error: Registration Un-Successful");
                    Log.d(TAG, "Volley Error: " + error.toString());
                    Toast.makeText(context, "Encountered Issue \nPlease Try Again", Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
            });

            queue.add(jsonObjectRequest);

        } catch (JSONException e) {
            Log.d(TAG,"Exception: Could not convert loginJson String to JsonObject");
            Log.d(TAG, "JSONException: " + e.toString());
            e.printStackTrace();
        }
    }
}
