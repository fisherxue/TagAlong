package com.tagalong.tagalong.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.security.crypto.EncryptedFile;
//import androidx.security.crypto.MasterKeys;

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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.tagalong.tagalong.Communication.FirebaseCallback;
import com.tagalong.tagalong.Models.Login;
import com.tagalong.tagalong.Models.Profile;
import com.tagalong.tagalong.R;
import com.tagalong.tagalong.Communication.VolleyCallback;
import com.tagalong.tagalong.Communication.VolleyCommunicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";

    //FaceBook Login Fields
    private CallbackManager callbackManager;
    private LoginButton fbloginButton;

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
        Log.d(TAG,"Main activity Created");
        context = getApplicationContext();

        //FaceBook login Fields Initiations
        callbackManager = CallbackManager.Factory.create();
        fbloginButton = (LoginButton) findViewById(R.id.fblogin_button);

        signupButton = (Button) findViewById(R.id.signup_button);
        loginButton = (Button) findViewById(R.id.login_button);
        loginPassword = (EditText) findViewById(R.id.passwordLogin);
        loginUser = (EditText) findViewById(R.id.userNameLogin);

        //Set up Fire-base notification manager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId  = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_id);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_HIGH));
            Log.d(TAG,"Fire-base notification manager set up");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Boolean needAuthentication;

        String profileFilename = "Saved_Profile.txt";
        StringBuffer stringBuffer = new StringBuffer();
        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(openFileInput(profileFilename)))) {
            String line = reader.readLine();
            while (line != null) {
                stringBuffer.append(line).append('\n');
                line = reader.readLine();
            }
        } catch (IOException e) {
            Log.d(TAG, "Could not load a saved profile");
            e.printStackTrace();
        } finally {
            String contents = stringBuffer.toString();
            needAuthentication = loadProfile(contents);
        }

        if (needAuthentication){
            Log.d(TAG,"No saved profile, need authentication");
            startAuthentication();
        }
    }

    private boolean loadProfile(String contents){
        boolean failedToLoad = false;
        JSONObject profileJSON;
        try {
            profileJSON = new JSONObject(contents);
            final Profile profile = new Profile();
            profile.setUserID(profileJSON.getString("userID"));
            profile.setUsername(profileJSON.getString("username"));
            profile.setFirstName(profileJSON.getString("firstName"));
            profile.setLastName(profileJSON.getString("lastName"));
            profile.setAge(profileJSON.getInt("age"));
            profile.setGender(profileJSON.getString("gender"));
            profile.setEmail(profileJSON.getString("email"));
            profile.setDriver(profileJSON.getBoolean("isDriver"));
            profile.setJoinedDate(profileJSON.getString("joinedDate"));
            JSONArray jsonArray = profileJSON.getJSONArray("interests");
            int [] interests = new int[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++){
                interests[i] = jsonArray.getInt(i);
            }
            profile.setInterests(interests);

            FirebaseCallback firebaseCallback = new FirebaseCallback() {
                @Override
                public void onSuccess(@NonNull Task<InstanceIdResult> task) {
                    String token = task.getResult().getToken();
                    profile.setFbToken(token);
                    loginSavedProfile(profile);
                }
            };
            getFCMToken(firebaseCallback);
        } catch (JSONException e) {
            Log.d(TAG, "Failed to convert stored json string to profile");
            Log.d(TAG, ("JSONException: " + e.toString()));
            failedToLoad = true;
        }

        return failedToLoad;
    }

    private void getFCMToken(final FirebaseCallback firebaseCallback){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "FCM failed", task.getException());
                            return;
                        }
                        Log.d(TAG,"Got FCM Device Token");
                        firebaseCallback.onSuccess(task);
                    }
                });

    }

    public void loginSavedProfile(final Profile profile){
        String url = getString(R.string.updateProfile);

        Gson gson = new Gson();
        String profileJson = gson.toJson(profile);
        JSONObject profileJsonObject;
        VolleyCommunicator communicator = VolleyCommunicator.getInstance(context.getApplicationContext());
        VolleyCallback callback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response){
                Log.d(TAG, "Saved login verification successful");
                Intent intent = new Intent(context, HomeActivity.class);
                intent.putExtra("profile", profile);
                startActivity(intent);
                MainActivity.this.finish();
            }

            @Override
            public void onError(String result){
                Log.d(TAG, "Saved login verification not successful");
                Toast.makeText(context, "Please try again", Toast.LENGTH_LONG).show();
            }

        };

        try {
            profileJsonObject = new JSONObject((profileJson));
            communicator.VolleyPut(url,profileJsonObject,callback);
        } catch (JSONException e) {
            Log.d(TAG, "Error making login JSONObject");
            Log.d(TAG, "JSONException: " + e.toString());
            e.printStackTrace();
        }
    }

    private void startAuthentication () {
        //FaceBook login Fields Initiations
        Log.d(TAG,"Starting authentication");
        startFBAuthentication();

        final Login login = new Login();

        final FirebaseCallback firebaseCallbackSignup = new FirebaseCallback() {
            @Override
            public void onSuccess(@NonNull Task<InstanceIdResult> task) {
                Login login = new Login();
                String token = task.getResult().getToken();
                login.setFbToken(token);
                Log.d(TAG,"Sign up new user");
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                intent.putExtra("login",login);
                startActivity(intent);
                MainActivity.this.finish();
            }
        };

        final FirebaseCallback firebaseCallbackLogin = new FirebaseCallback() {
            @Override
            public void onSuccess(@NonNull Task<InstanceIdResult> task) {
                String token = task.getResult().getToken();
                login.setFbToken(token);
                verifyUser(login, false);
            }
        };

        signupButton.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Sign up requested");
                getFCMToken(firebaseCallbackSignup);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean allSet = true;

                if (!loginPassword.getText().toString().isEmpty()){
                    login.setPassword(loginPassword.getText().toString());
                    Log.d(TAG,"password entered");
                }
                else {
                    Toast.makeText(context, "Please Enter Username", Toast.LENGTH_LONG).show();
                    allSet = false;
                    Log.d(TAG,"password not entered");
                }

                if (!loginUser.getText().toString().isEmpty()){
                    login.setUsername(loginUser.getText().toString());
                    Log.d(TAG,"username entered");
                }
                else {
                    Toast.makeText(context, "Please Enter password", Toast.LENGTH_LONG).show();
                    allSet = false;
                    Log.d(TAG,"username not entered");
                }

                if (allSet) {
                    getFCMToken(firebaseCallbackLogin);
                }
            }
        });
    }

    private void startFBAuthentication(){
        final FirebaseCallback firebaseCallbackFB = new FirebaseCallback() {
            @Override
            public void onSuccess(@NonNull Task<InstanceIdResult> task) {
                String token = task.getResult().getToken();
                handleFacebookLogin(token);
            }
        };

        fbloginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "Successful FaceBook login");
                getFCMToken(firebaseCallbackFB);
            }
            @Override
            public void onCancel() {
                Log.d(TAG, "FaceBook login cancelled");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d(TAG, "Error in login using FaceBook: check the network");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookLogin(final String fcmToken){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null) {
            Toast.makeText(context, "Some error, please re-login and try again", Toast.LENGTH_LONG).show();
            Log.d(TAG, "null FB token");
        }
        else {
            GraphRequest graphRequest = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    Login fbLoginProfile = new Login();
                    try {
                        fbLoginProfile.setUsername(object.getString("first_name") + " " + object.getString("last_name"));
                        fbLoginProfile.setId(object.getString("id"));
                        fbLoginProfile.setFirstName(object.getString("first_name"));
                        fbLoginProfile.setLastName(object.getString("last_name"));
                        fbLoginProfile.setEmailId(object.getString("email"));
                        fbLoginProfile.setPassword(object.getString("id"));
                        fbLoginProfile.setFbToken(fcmToken);
                    }
                    catch (JSONException e) {
                        Log.d(TAG, "Failed to retrieve profile while facebook login");
                        Log.d(TAG, "JSONException: " + e.toString());
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

    private void verifyUser(final Login login, final boolean isExternal){
        Log.d(TAG,"verifying user login details");
        String url = getString(R.string.login);

        final Gson gson = new Gson();
        final String loginJson = gson.toJson(login);
        JSONObject loginJsonObject;

        VolleyCommunicator communicator = VolleyCommunicator.getInstance(context.getApplicationContext());
        VolleyCallback callback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response){
                Log.d(TAG, "Login verification successful");
                verifyUserSuccess(response);
            }

            @Override
            public void onError(String result){
                verifyUserError(login,isExternal);
            }

        };

        try {
            loginJsonObject = new JSONObject((loginJson));
            communicator.VolleyPost(url,loginJsonObject,callback);
        } catch (JSONException e) {
            Log.d(TAG, "Error making login JSONObject");
            Log.d(TAG, "JSONException: " + e.toString());
            e.printStackTrace();
        }

    }

    private void verifyUserSuccess(JSONObject response){
        Profile profile = new Profile();
        try {
            profile.setUsername(response.getString("username"));
            profile.setUserID(response.getString("_id"));
            profile.setFirstName(response.getString("firstName"));
            profile.setLastName(response.getString("lastName"));
            profile.setAge(response.getInt("age"));
            profile.setGender(response.getString("gender"));
            profile.setEmail(response.getString("email"));
            profile.setDriver(response.getBoolean("isDriver"));
            profile.setJoinedDate(response.getString("joinedDate"));

            JSONArray jsonArray = response.getJSONArray("interests");
            int [] interests = new int[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++){
                interests[i] = jsonArray.getInt(i);
            }
            profile.setInterests(interests);
        } catch (JSONException e) {
            Log.d(TAG, "Failed to retrieve profile while login");
            Log.d(TAG, "JSONException: " + e.toString());
        }

        Toast.makeText(context, "Successfully logged in", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        intent.putExtra("profile", profile);
        startActivity(intent);
        MainActivity.this.finish();
    }

    private void verifyUserError(Login login, boolean isExternal) {
        if (isExternal) {
            Log.d(TAG, "Login verification not successful, new external user discovered");
            Login newLogin = new Login();
            newLogin.setUsername(login.getUsername());
            newLogin.setPassword(login.getId());
            newLogin.setId(login.getId());
            newLogin.setFirstName(login.getFirstName());
            newLogin.setLastName(login.getLastName());
            newLogin.setEmailId(login.getEmailId());
            newUserLogin(newLogin);
        }
        else {
            Log.d(TAG, "Login verification not successful");
            Toast.makeText(context, "Incorrect Username or Password", Toast.LENGTH_LONG).show();
        }
    }

    private void newUserLogin(Login newLogin){
        Log.d(TAG,"Sending new external login details");
        String url = getString(R.string.register);

        final Gson gson = new Gson();
        final String loginJson = gson.toJson(newLogin);
        JSONObject loginJsonObject;

        VolleyCommunicator communicator = VolleyCommunicator.getInstance(context.getApplicationContext());
        VolleyCallback callback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response){
                Log.d(TAG, "New external login successful");
                Profile profile = new Profile();
                Toast.makeText(context, "Successfully Logged in", Toast.LENGTH_LONG).show();

                try {
                    profile.setUsername(response.getString("username"));
                    profile.setEmail(response.getString("email"));
                    profile.setUserID(response.getString("_id"));
                    profile.setJoinedDate(response.getString("joinedDate"));
                } catch (JSONException e) {
                    Log.d(TAG, "Failed to retrieve profile while login");
                    Log.d(TAG, "JSONException: " + e.toString());
                }

                Intent intent = new Intent(context, UpdateProfileActivity.class);
                intent.putExtra("profile", profile);
                startActivity(intent);
            }

            @Override
            public void onError(String result){
                Log.d(TAG, "Registration not successful");
                Log.d(TAG, "VolleyError: " + result);
                Toast.makeText(context, "Please Try Again", Toast.LENGTH_LONG).show();
            }

        };

        try {
            loginJsonObject = new JSONObject((loginJson));
            communicator.VolleyPost(url,loginJsonObject,callback);
        } catch (JSONException e) {
            Log.d(TAG, "Error making new login JSONObject");
            Log.d(TAG, "Exception" + e.toString());
            e.printStackTrace();
        }
    }
}
