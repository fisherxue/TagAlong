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
        Log.d(TAG,"Started Signup Activity");

        context = getApplicationContext();
        login = (Login) getIntent().getSerializableExtra("login");

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
        String url = getString(R.string.register);

        final Gson gson = new Gson();
        final String profileJson = gson.toJson(loginProfile);
        JSONObject profileJsonObject;

        VolleyCommunicator communicator = VolleyCommunicator.getInstance(context.getApplicationContext());
        VolleyCallback callback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response){
                Log.d(TAG, "Login verification successful");
                loginSuccess(response);
            }
            @Override
            public void onError(String result){
                Log.d(TAG,"Registration not successful");
                Log.d(TAG, "Volley Error: " + result);
                Toast.makeText(context, "Encountered Issue \nPlease Try Again", Toast.LENGTH_LONG).show();
            }

        };

        try {
            profileJsonObject = new JSONObject((profileJson));
            communicator.VolleyPost(url,profileJsonObject,callback);
        } catch (JSONException e) {
            Log.d(TAG, "Error making signup profile JSONObject");
            Log.d(TAG, "JSONException: " + e.toString());
            e.printStackTrace();
        }
    }

    private void loginSuccess (JSONObject response) {
        Log.d(TAG, "Login Registration Successful");
        Profile receivedProfile = new Profile();
        Toast.makeText(context, "Successfully Logged in", Toast.LENGTH_LONG).show();

        try {
            receivedProfile.setUsername(response.getString("username"));
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
        SignupActivity.this.finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
        SignupActivity.this.finish();
    }
}
