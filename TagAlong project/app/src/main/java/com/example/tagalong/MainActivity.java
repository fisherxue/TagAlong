package com.example.tagalong;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";

    private CallbackManager callbackManager;
    private LoginButton fbloginButton;
    private Button loginButton;
    private  Button signupButton;

    AccessTokenTracker accessTokenTracker;
    AccessToken accessToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        callbackManager = CallbackManager.Factory.create();

        fbloginButton = (LoginButton) findViewById(R.id.fblogin_button);
        signupButton = (Button) findViewById(R.id.signup_button);
        loginButton = (Button) findViewById(R.id.login_button);

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
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
                MainActivity.this.finish();
            }
        });

        /*
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://ec2-50-17-82-63.compute-1.amazonaws.com/";

        final TextView textView = (TextView) findViewById(R.id.text);

    // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean  varifyUser(){
        return false;
    }

    private void handleFacebookLogin(){
        getUserData();
        boolean isNew = varifyUser();

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
                    System.out.println(object);
                }
            });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,email,first_name");
            graphRequest.setParameters(parameters);
            graphRequest.executeAsync();
        }
    }
}


