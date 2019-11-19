package com.tagalong.tagalong;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity {

    private String TAG = "Signup Activity";
    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText passwordConfirmET;
    private EditText emailEditText;
    private Button nextButton;

    private Context context;
    private Login login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        context = getApplicationContext();
        login = (Login) getIntent().getSerializableExtra("login");

        Log.d(TAG,"Started Signup Activity");

        usernameEditText = (EditText) findViewById(R.id.username);
        passwordEditText = (EditText) findViewById(R.id.password);
        passwordConfirmET = (EditText) findViewById(R.id.password2);
        nextButton = (Button) findViewById(R.id.nextbutton);
        emailEditText = (EditText) findViewById(R.id.Email);
    }

    @Override
    protected void onStart() {
        super.onStart();
        final Login newUserLogin = new Login();
        nextButton.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       boolean allSet;
                       allSet = true;

                       if (!usernameEditText.getText().toString().isEmpty()) {
                           newUserLogin.setUsername(usernameEditText.getText().toString());
                           Log.d(TAG,"Username set");
                       }
                       else {
                           Toast.makeText(context, "Please Enter Username", Toast.LENGTH_LONG).show();
                           allSet = false;
                           Log.d(TAG,"Username not set");
                       }

                       if (!passwordEditText.getText().toString().isEmpty()) {
                           if (passwordEditText.getText().toString().equals(passwordConfirmET.getText().toString())) {
                               newUserLogin.setPassword(passwordEditText.getText().toString());
                               Log.d(TAG,"Password set");
                           }
                           else {
                               Toast.makeText(context, "Passwords Do Not Match", Toast.LENGTH_LONG).show();
                               allSet = false;
                               Log.d(TAG,"Passwords do not match");
                           }
                       }
                       else {
                           Toast.makeText(context, "Please Enter Password", Toast.LENGTH_LONG).show();
                           allSet = false;
                           Log.d(TAG,"Password not set");
                       }

                       if (!emailEditText.getText().toString().isEmpty()) {
                           if (emailEditText.getText().toString().matches("^(.+)@(.+)$")) {
                               newUserLogin.setEmailId(emailEditText.getText().toString());
                               Log.d(TAG,"Email Set");
                           }
                           else {
                               Toast.makeText(context, "Please Enter Valid Email", Toast.LENGTH_LONG).show();
                               allSet = false;
                               Log.d(TAG,"Email not valid");
                           }
                       } else {
                           Toast.makeText(context, "Please Enter Email", Toast.LENGTH_LONG).show();
                           allSet = false;
                           Log.d(TAG,"Email not set");
                       }

                       newUserLogin.setFbToken(login.getFbToken());

                       if (allSet) {
                            sendLogin(newUserLogin);
                       }
                   }
               }
            );
    }

    private void sendLogin(Login loginProfile){

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getString(R.string.register);

        final Gson gson = new Gson();
        final String loginProfileJson = gson.toJson(loginProfile);
        JSONObject profileJsonObject;

        try {
            Log.d(TAG,"Registering login information");
            profileJsonObject = new JSONObject((loginProfileJson));
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, profileJsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "Login Registration Successful");
                    Profile receivedProfile = new Profile();
                    Toast.makeText(context, "Successfully Logged in", Toast.LENGTH_LONG).show();

                    try {
                        receivedProfile.setUserName(response.getString("username"));
                    } catch (JSONException e) {
                        Log.d(TAG, "Error getting username from received profile");
                        e.printStackTrace();
                    }

                    try {
                        receivedProfile.setPassword(response.getString("password"));
                    } catch (JSONException e) {
                        Log.d(TAG, "Error getting password from received profile");
                        e.printStackTrace();
                    }

                    try {
                        receivedProfile.setEmail(response.getString("email"));
                    } catch (JSONException e) {
                        Log.d(TAG, "Error getting email from received profile");
                        e.printStackTrace();
                    }

                    try {
                        receivedProfile.setUserID(response.getString("_id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(TAG, "Error getting  user id from profile");
                    }

                    try {
                        receivedProfile.setJoinedDate(response.getString("joinedDate"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(TAG, "Error getting joinedDate from received profile");
                    }

                    Log.d(TAG,"Successfully retrieved login profile information");
                    Intent intent = new Intent(context, UpdateProfileActivity.class);
                    intent.putExtra("profile", receivedProfile);
                    startActivity(intent);
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
