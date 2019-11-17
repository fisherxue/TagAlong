package com.tagalong.tagalong;

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
//import android.security.keystore.KeyGenParameterSpec;
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
//import com.facebook.AccessTokenTracker;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
//import java.security.GeneralSecurityException;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";

    //FaceBook Login Fields
    private CallbackManager callbackManager;
    private LoginButton fbloginButton;
    //private AccessTokenTracker accessTokenTracker;
    //private AccessToken accessToken;

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
        Boolean needAuthenication = false;

        /*
        KeyGenParameterSpec keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC;
        String masterKeyAlias = null;

        try {
            masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec);
            Log.d(TAG, "key: " + masterKeyAlias);
        } catch (GeneralSecurityException e) {
            Log.d(TAG, "Error : creating key for encryption");
            Log.d(TAG, "Error (General Security Exception): " + e.toString());
            needAuthenication = true;
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG, "Error : creating key for encryption");
            Log.d(TAG, "Error (IOException): " + e.toString());
            needAuthenication = true;
            e.printStackTrace();
        }

        String fileToRead = "Saved_Profile.txt";
        EncryptedFile encryptedFile = null;
        try {
            encryptedFile = new EncryptedFile.Builder(
                    new File(context.getFilesDir(), fileToRead),
                    context,
                    masterKeyAlias,
                    EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build();

        } catch (GeneralSecurityException e) {
            Log.d(TAG, "Error : opening encryption file");
            Log.d(TAG, "Error (General Security Exception): " + e.toString());
            needAuthenication = true;
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG, "Error : opening encrypted file");
            Log.d(TAG, "Error (IOException): " + e.toString());
            needAuthenication = true;
            e.printStackTrace();
        }

        if (encryptedFile != null) {
            StringBuffer stringBuffer = new StringBuffer();
            try (BufferedReader reader =
                         new BufferedReader(new InputStreamReader(encryptedFile.openFileInput()))) {

                String line = reader.readLine();
                while (line != null) {
                    stringBuffer.append(line).append('\n');
                    line = reader.readLine();
                }
            } catch (IOException e) {
                Log.d(TAG, "Error : reading encrypted file");
                Log.d(TAG, "Error (IOException): " + e.toString());
                needAuthenication = true;
                e.printStackTrace();
            } catch (GeneralSecurityException e) {
                Log.d(TAG, "Error : reading encryption file");
                Log.d(TAG, "Error (General Security Exception): " + e.toString());
                needAuthenication = true;
                e.printStackTrace();
            } finally {
                String contents = stringBuffer.toString();
                needAuthenication = loadProfile(contents);
            }
        }

         */

        String filename = "Saved_Profile.txt";
        StringBuffer stringBuffer = new StringBuffer();
        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(openFileInput(filename)))) {

            String line = reader.readLine();
            while (line != null) {
                stringBuffer.append(line).append('\n');
                line = reader.readLine();
            }
        } catch (IOException e) {
            Log.d(TAG, "Error : reading encrypted file");
            Log.d(TAG, "Error (IOException): " + e.toString());
            needAuthenication = true;
            e.printStackTrace();
        } finally {
            String contents = stringBuffer.toString();
            needAuthenication = loadProfile(contents);
        }


        if (needAuthenication){
            startAuthentication();
        }
    }

    private boolean loadProfile(String contents){
        boolean failedToLoad = false;
        JSONObject profileJSON;
        try {
            profileJSON = new JSONObject(contents);
            final Profile profile = new Profile();

            profile.setUserName(profileJSON.getString("username"));
            JSONArray jsonArray = profileJSON.getJSONArray("interests");
            int [] interests = new int[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++){
                interests[i] = jsonArray.getInt(i);
            }
            profile.setInterests(interests);
            profile.setAge(profileJSON.getInt("age"));
            profile.setGender(profileJSON.getString("gender"));
            profile.setEmail(profileJSON.getString("email"));
            profile.setPassword(profileJSON.getString("password"));
            profile.setDriver(profileJSON.getBoolean("isDriver"));
            profile.setUserID(profileJSON.getString("userID"));
            profile.setJoinedDate(profileJSON.getString("joinedDate"));
            profile.setFirstName(profileJSON.getString("firstName"));
            profile.setLastName(profileJSON.getString("lastName"));

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
                            profile.setFbToken(token);
                            sendFCM(profile);
                        }
                    });


        } catch (JSONException e) {
            Log.d(TAG, "Error : failed to convert stored json string to profile");
            Log.d(TAG, "Error (JSONException): " + e.toString());
            e.printStackTrace();
            failedToLoad = true;
        }

        return failedToLoad;
    }

    public void sendFCM(final Profile profile){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = getString(R.string.updateProfile);
        Gson gson = new Gson();
        String profileJson = gson.toJson(profile);
        JSONObject profileJsonObject;
        try {
            profileJsonObject = new JSONObject((profileJson));
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, profileJsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Intent intent = new Intent(context, HomeActivity.class);
                    intent.putExtra("profile", profile);
                    startActivity(intent);
                    MainActivity.this.finish();

                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, "Please try again", Toast.LENGTH_LONG).show();
                }
            });

            queue.add(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void startAuthentication () {
        //FaceBook login Fields Initiations
        callbackManager = CallbackManager.Factory.create();
        fbloginButton = (LoginButton) findViewById(R.id.fblogin_button);

        signupButton = (Button) findViewById(R.id.signup_button);
        loginButton = (Button) findViewById(R.id.login_button);
        loginPassword = (EditText) findViewById(R.id.passwordLogin);
        loginUser = (EditText) findViewById(R.id.userNameLogin);

        startFBAuthentication();

        //Click on signupButton
        signupButton.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Log.d(TAG, "Sign up requested");
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                Login loginProfile = new Login();
                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "getInstanceId failed", task.getException());
                                    return;
                                }
                                String token = task.getResult().getToken();
                                Log.d(TAG, token);
                                loginProfile.setFbToken(token);

                                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                                intent.putExtra("login",loginProfile);
                                startActivity(intent);
                                Log.d(TAG, "Main Activity Ended");
                            }
                        });
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

    private void startFBAuthentication(){
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookLogin(final String fcmToken){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null) {
            Log.d(TAG, "Some Error, please re-login and try again");
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
        String url = getString(R.string.login);
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
                        JSONArray jsonArray = response.getJSONArray("interests");
                        int [] interests = new int[jsonArray.length()];
                        for (int i = 0; i < jsonArray.length(); i++){
                            interests[i] = jsonArray.getInt(i);
                        }
                        receivedProfile.setInterests(interests);
                        receivedProfile.setAge(response.getInt("age"));
                        receivedProfile.setGender(response.getString("gender"));
                        receivedProfile.setEmail(response.getString("email"));
                        receivedProfile.setPassword(response.getString("password"));
                        receivedProfile.setDriver(response.getBoolean("isDriver"));
                        receivedProfile.setUserID(response.getString("_id"));
                        receivedProfile.setJoinedDate(response.getString("joinedDate"));
                        receivedProfile.setFirstName(response.getString("firstName"));
                        receivedProfile.setLastName(response.getString("lastName"));
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
                        newLoginProfile.setPassword(loginProfile.getId());
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
        String url = getString(R.string.register);
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

                    try {
                        receivedProfile.setUserName(response.getString("username"));
                        receivedProfile.setPassword(response.getString("password"));
                        receivedProfile.setEmail(response.getString("email"));
                        receivedProfile.setUserID(response.getString("_id"));
                        receivedProfile.setJoinedDate(response.getString("joinedDate"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(context, UpdateProfileActivity.class);
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
