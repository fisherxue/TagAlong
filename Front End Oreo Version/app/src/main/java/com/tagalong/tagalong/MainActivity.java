package com.tagalong.tagalong;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";

    //FaceBook Login Fields
    private CallbackManager callbackManager;
    private LoginButton fbloginButton;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken accessToken;

    //Login-SignUp Fields
    private Button loginButton;
    private Button signupButton;
    private EditText loginUser;
    private EditText loginPassword;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

        //FaceBook login Fields Initiations
        callbackManager = CallbackManager.Factory.create();
        fbloginButton = (LoginButton) findViewById(R.id.fblogin_button);

        signupButton = (Button) findViewById(R.id.signup_button);
        loginButton = (Button) findViewById(R.id.login_button);
        loginPassword = (EditText) findViewById(R.id.passwordLogin);
        loginUser = (EditText) findViewById(R.id.userNameLogin);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_id);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_HIGH));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Implement the part if user is already logged in

        startAuthentication();
    }


    private void startAuthentication () {
        //FaceBook login Fields Initiations
        callbackManager = CallbackManager.Factory.create();
        fbloginButton = (LoginButton) findViewById(R.id.fblogin_button);

        signupButton = (Button) findViewById(R.id.signup_button);
        loginButton = (Button) findViewById(R.id.login_button);
        loginPassword = (EditText) findViewById(R.id.passwordLogin);
        loginUser = (EditText) findViewById(R.id.userNameLogin);

        //Click on FaceBook Button
        fbloginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "Successful FaceBook Login");
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "getInstanceId failed", task.getException());
                                    return;
                                }

                                String token = task.getResult().getToken();
                                Log.d(TAG, token);

                                handleFacebookLogin(token);
                            }
                        });
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "FaceBook Login Cancelled");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d(TAG, "Error In Login using FaceBook: Check the Network");
            }
        });

        //Click on signupButton
        signupButton.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Log.d(TAG, "Sign up requested");
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
                Log.d(TAG, "Main Activity Ended");
                //MainActivity.this.finish();
            }
        });

        //Click on LoginButton
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Login loginProfile = new Login();
                boolean allSet = true;

                if (!loginPassword.getText().toString().isEmpty()){
                    loginProfile.setPassword(loginPassword.getText().toString());
                }
                else {
                    Toast.makeText(context, "Please Enter Username", Toast.LENGTH_LONG).show();
                    allSet = false;
                }


                if (!loginUser.getText().toString().isEmpty()){
                    loginProfile.setUsername(loginUser.getText().toString());
                }
                else {
                    Toast.makeText(context, "Please Enter password", Toast.LENGTH_LONG).show();
                    allSet = false;
                }

                if (allSet) {
                    FirebaseInstanceId.getInstance().getInstanceId()
                            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                @Override
                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                    if (!task.isSuccessful()) {
                                        Log.w(TAG, "getInstanceId failed", task.getException());
                                        return;
                                    }
                                    String token = task.getResult().getToken();
                                    Log.d(TAG, token);
                                    loginProfile.setFbToken(token);
                                    verifyUser(loginProfile, false);
                                }
                            });
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookLogin(final String fcmToken){
        accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null) {
            Log.d(TAG, "Some Error, please re-login and try again");
        }
        else {
            GraphRequest graphRequest = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    Login fbLoginProfile = new Login();
                    Profile facebookProfile = new Profile();
                    try {
                        fbLoginProfile.setUsername(object.getString("first_name") + " " + object.getString("last_name"));
                        fbLoginProfile.setId(object.getString("id"));
                        fbLoginProfile.setFirstName(object.getString("first_name"));
                        fbLoginProfile.setLastName(object.getString("last_name"));
                        fbLoginProfile.setEmailId(object.getString("email"));
                        fbLoginProfile.setFbToken(fcmToken);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                    verifyUser(fbLoginProfile, true);
                }
            });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,email,first_name,last_name");
            graphRequest.setParameters(parameters);
            graphRequest.executeAsync();
        }
    }

    private void verifyUser(final Login loginProfile, final boolean isExternal){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://206.87.96.130:3000/users/login";
        final Gson gson = new Gson();
        final String loginProfileJson = gson.toJson(loginProfile);
        JSONObject profileJsonObject;

        try {
            profileJsonObject = new JSONObject((loginProfileJson));
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, profileJsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "Login Verification Successful");
                    Profile receivedProfile = new Profile();

                    try {
                        receivedProfile.setUserName(response.getString("username"));
                        receivedProfile.setInterest(response.getString("interests"));
                        receivedProfile.setFirstName(response.getString("firstName"));
                        receivedProfile.setLastName(response.getString("lastName"));
                        receivedProfile.setAge(response.getInt("age"));
                        receivedProfile.setGender(response.getString("gender"));
                        receivedProfile.setEmail(response.getString("email"));
                        receivedProfile.setPassword(response.getString("password"));
                        receivedProfile.setDriver(response.getBoolean("isDriver"));
                        receivedProfile.set_id(response.getString("_id"));
                        receivedProfile.setJoinedDate(response.getString("joinedDate"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(context, "Successfully Logged in", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    intent.putExtra("profile", receivedProfile);
                    startActivity(intent);
                    MainActivity.this.finish();
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (isExternal) {
                        Log.d(TAG, "Login Verification Un-Successful, New External User");
                        Login newLoginProfile = new Login();
                        newLoginProfile.setUsername(loginProfile.getUsername());
                        newLoginProfile.setId(loginProfile.getId());
                        newLoginProfile.setFirstName(loginProfile.getFirstName());
                        newLoginProfile.setLastName(loginProfile.getLastName());
                        newLoginProfile.setEmailId(loginProfile.getEmailId());
                        sendLogin(newLoginProfile);
                    }
                    else {
                        Log.d(TAG, "Login Verification Un-Successful");
                        Toast.makeText(context, "UserName or Incorrect Password", Toast.LENGTH_LONG).show();
                    }
                }
            });

            queue.add(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void sendLogin(Login loginProfile){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://206.87.96.130:3000/users/login";
        final Gson gson = new Gson();
        final String loginProfileJson = gson.toJson(loginProfile);
        JSONObject profileJsonObject;

        try {
            profileJsonObject = new JSONObject((loginProfileJson));
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, profileJsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d(TAG, "Login Verification Successful");
                    Profile receivedProfile = new Profile();
                    Toast.makeText(context, "Successfully Logged in", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(context, Login.class);
                    intent.putExtra("profile", receivedProfile);
                    startActivity(intent);
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "Registration Un-Successful");
                    Toast.makeText(context, "Please Try Again", Toast.LENGTH_LONG).show();
                }
            });

            queue.add(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}