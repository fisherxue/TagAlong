package com.example.tagalong;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
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
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";

    private CallbackManager callbackManager;
    private LoginButton fbloginButton;
    private Button loginButton;
    private  Button signupButton;
    private EditText loginUser, loginPassword;
    private Context context;
    private boolean successLogin;
    Profile recieved_profile;
    Profile facebook_login;
    AccessTokenTracker accessTokenTracker;
    AccessToken accessToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        callbackManager = CallbackManager.Factory.create();

        fbloginButton = (LoginButton) findViewById(R.id.fblogin_button);
        signupButton = (Button) findViewById(R.id.signup_button);
        loginButton = (Button) findViewById(R.id.login_button);
        loginPassword = (EditText) findViewById(R.id.passwordLogin);
        loginUser = (EditText) findViewById(R.id.userNameLogin);

        fbloginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "Successful Login");
                handleFacebookLogin();
                //Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                //startActivity(intent);
                //MainActivity.this.finish();
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "Login Cancelled");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d(TAG, "Error In Login: Check the Network");
            }
        });

        signupButton.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Signup.class);
                startActivity(intent);
                MainActivity.this.finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Profile loginProfile = new Profile();
                boolean allSet = true;
                if (!loginPassword.getText().toString().isEmpty()){
                    loginProfile.setPassword(loginPassword.getText().toString());
                }
                else {
                    Toast.makeText(context, "Please Enter Username", Toast.LENGTH_LONG).show();
                    allSet = false;
                }
                if (!loginUser.getText().toString().isEmpty()){
                    loginProfile.setUserName(loginUser.getText().toString());
                }
                else {
                    Toast.makeText(context, "Please Enter password", Toast.LENGTH_LONG).show();
                    allSet = false;
                }

                if (allSet) {
                    varifyUser(loginProfile, false); /*
                    if(varifyUser(loginProfile)){
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        intent.putExtra("profile", recieved_profile);
                        startActivity(intent);
                        MainActivity.this.finish();
                    } */
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean varifyUser(final Profile profile, final boolean isExternal){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://206.87.96.130:3000/users/login";
        final Gson gson = new Gson();
        final String profileJson = gson.toJson(profile);
        JSONObject profileJsonObject;
        successLogin = false;
        try {
            profileJsonObject = new JSONObject((profileJson));
            System.out.println(profileJson.toString());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, profileJsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println(response.toString());
                    try {
                        recieved_profile = new Profile();
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

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(context, "Successfully Logged in", Toast.LENGTH_LONG).show();
                    successLogin = true;
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    intent.putExtra("profile", recieved_profile);
                    startActivity(intent);
                    MainActivity.this.finish();
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (isExternal) {
                        recieved_profile = new Profile();
                        recieved_profile.setUserName(profile.getUserName());
                        recieved_profile.setPassword(profile.get_id());
                        recieved_profile.setFirstName(profile.getFirstName());
                        recieved_profile.setLastName(profile.getLastName());

                        Intent intent = new Intent(MainActivity.this, Signup.class);
                        intent.putExtra("profile", recieved_profile);
                        startActivity(intent);
                        MainActivity.this.finish();
                        LoginManager.getInstance().logOut();
                    }
                    else {
                        Toast.makeText(context, "UserName or Incorrect Password", Toast.LENGTH_LONG).show();
                        successLogin = false;
                    }
                }
            });

            queue.add(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return successLogin;
    }

    private void handleFacebookLogin(){
        getUserData();
        boolean isNew = false;

        if (isNew){
            Intent intent = new Intent(MainActivity.this, Signup.class);
            startActivity(intent);
            MainActivity.this.finish();
        } else {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            MainActivity.this.finish();
        }
    }

    private void getUserData(){
        accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null) {
            Log.d(TAG, "Some Error, please re-login and try again");
        }
        else {
            GraphRequest graphRequest = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    Profile facebookProfile = new Profile();
                    try {
                        facebookProfile.setUserName(object.getString("first_name") + " " + object.getString("last_name"));
                        facebookProfile.setPassword(object.getString("id"));
                        facebookProfile.setFirstName(object.getString("first_name"));
                        facebookProfile.setLastName(object.getString("last_name"));
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                    varifyUser(facebookProfile, true);
                }
            });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,email,first_name,last_name");
            graphRequest.setParameters(parameters);
            graphRequest.executeAsync();
        }
    }
}


