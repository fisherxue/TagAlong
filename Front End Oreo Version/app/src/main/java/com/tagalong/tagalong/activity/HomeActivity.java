package com.tagalong.tagalong.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.facebook.login.LoginManager;
import com.google.gson.JsonObject;
import com.tagalong.tagalong.communication.FirebaseCallback;
import com.tagalong.tagalong.communication.VolleyCallback;
import com.tagalong.tagalong.communication.VolleyCommunicator;
import com.tagalong.tagalong.fragment.MyTripFragment;
import com.tagalong.tagalong.models.Profile;
import com.tagalong.tagalong.fragment.ProposedTripFragment;
import com.tagalong.tagalong.R;
import com.tagalong.tagalong.fragment.SetTripFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomeActivity extends AppCompatActivity {
    private final String TAG = "Home Activity";
    private Context context;
    private Profile userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG,"Home activity created");

        context = this;
        userProfile = (Profile) getIntent().getSerializableExtra("profile");
        if (userProfile == null) {
            loadProfile();
        }
        else {
            saveUserProfile();
            loadupMyTripFragmenet();
        }

    }

    private void loadupMyTripFragmenet() {
        BottomNavigationView btv = findViewById(R.id.bottom_navigation);
        btv.setOnNavigationItemSelectedListener(lister);

        Fragment frag = new MyTripFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("profile", userProfile);
        frag.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, frag).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile :
                Log.d(TAG,"Visualizing user profile.");
                Intent intent = new Intent(context, ViewProfileActivity.class);
                intent.putExtra("profile", userProfile);
                startActivity(intent);
                break;
            case R.id.logout :
                Log.d(TAG,"Logging out of account.");
                LoginManager.getInstance().logOut();

                String url = getString(R.string.logout);

                JsonObject logout = new JsonObject();
                logout.addProperty("userID",userProfile.getUserID());
                final String logoutJson = logout.toString();
                JSONObject logoutJsonObject;

                VolleyCommunicator communicator = VolleyCommunicator.getInstance(context.getApplicationContext());
                VolleyCallback callback = new VolleyCallback() {
                    @Override
                    public void onSuccess(JSONObject response){
                        Log.d(TAG, "Logout Successful");
                        removeSavedFiles();
                        Intent intent2 = new Intent(context, MainActivity.class);
                        startActivity(intent2);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        HomeActivity.this.finish();
                    }

                    @Override
                    public void onError(String result){
                        Log.d(TAG, "Could not logout");
                        Log.d(TAG, "Error: " + result);
                        Toast.makeText(context, "We encountered some error,\nPlease logout again", Toast.LENGTH_LONG).show();
                    }

                };
                try {
                    logoutJsonObject = new JSONObject(logoutJson);
                    communicator.volleyPost(url,logoutJsonObject,callback);
                } catch (JSONException e) {
                    Log.d(TAG, "Error making logout JSONObject");
                    Log.d(TAG, "JSONException: " + e.toString());
                    e.printStackTrace();
                }



                break;
            default :
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener lister =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment fragment = null;

                    switch (menuItem.getItemId()){
                        case R.id.nav_home:
                            Log.d(TAG,"Opening MyTripFragment");
                            fragment = new MyTripFragment();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("profile", userProfile);
                            fragment.setArguments(bundle);
                            break;

                        case R.id.nav_maps:
                            Log.d(TAG,"Opening SetTripFragment");
                            Intent intent = new Intent(context, SetTripFragment.class);
                            intent.putExtra("profile", userProfile);
                            startActivity(intent);
                            HomeActivity.this.finish();
                            break;

                        case R.id.nav_chat:
                            Log.d(TAG,"Opening ProposedTripFragment");
                            fragment = new ProposedTripFragment();
                            Bundle bundle2 = new Bundle();
                            bundle2.putSerializable("profile", userProfile);
                            fragment.setArguments(bundle2);
                            break;

                        default:
                            break;
                    }
                    if (fragment != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                    }
                    return true;
                }
            };

    private void removeSavedFiles(){
        context.deleteFile("Saved_Profile.txt");
        userProfile = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveUserProfile();
    }

    private void saveUserProfile() {
        if (userProfile != null) {
            String filename = "Saved_Profile.txt";
            Gson gson = new Gson();
            String profileJson = gson.toJson(userProfile);
            String fileContents = profileJson;
            FileOutputStream outputStream;

            try {
                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                outputStream.write(fileContents.getBytes());
                outputStream.close();
            } catch (Exception e) {
                Log.d(TAG, "Exception writing saved profile file");
                Log.d(TAG, "Exception: " + e.toString());
                e.printStackTrace();
            } finally {
                Log.d(TAG, "Saved profile in Saved_Profile.txt");
            }
        }
    }

    private void loadProfile(){
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
            Log.d(TAG, "Could not load a saved profile.");
            Toast.makeText(context, "Sorry we faced some issue,\n Please reload the page", Toast.LENGTH_LONG).show();
            return ;
        }

        String contents = stringBuffer.toString();
        JSONObject profileJSON;
        try {
            profileJSON = new JSONObject(contents);
            System.out.println(profileJSON);
            this.userProfile = new Profile();
            userProfile.setCarCapacity(profileJSON.getInt("carCapacity"));
            userProfile.setUserID(profileJSON.getString("userID"));
            userProfile.setUsername(profileJSON.getString("username"));
            userProfile.setFirstName(profileJSON.getString("firstName"));
            userProfile.setLastName(profileJSON.getString("lastName"));
            userProfile.setAge(profileJSON.getInt("age"));
            userProfile.setGender(profileJSON.getString("gender"));
            userProfile.setEmail(profileJSON.getString("email"));
            userProfile.setDriver(profileJSON.getBoolean("isDriver"));
            userProfile.setJoinedDate(profileJSON.getString("joinedDate"));
            JSONArray jsonArray = profileJSON.getJSONArray("interests");
            int [] interests = new int[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++){
                interests[i] = jsonArray.getInt(i);
            }
            userProfile.setInterests(interests);

            FirebaseCallback firebaseCallback = new FirebaseCallback() {
                @Override
                public void onSuccess(@NonNull Task<InstanceIdResult> task) {
                    String token = task.getResult().getToken();
                    userProfile.setFbToken(token);
                    loginSavedProfile();
                }
            };
            getFCMToken(firebaseCallback);
        } catch (JSONException e) {
            Toast.makeText(context, "Sorry we faced some issue,\n Please reload the page", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Failed to convert stored json string to profile");
            Log.d(TAG, ("JSONException: " + e.toString()));
            this.userProfile = null;
            return ;
        }
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

    private void loginSavedProfile(){
        String url = getString(R.string.updateProfile);
        Gson gson = new Gson();
        String profileJson = gson.toJson(userProfile);
        JSONObject profileJsonObject;
        VolleyCommunicator communicator = VolleyCommunicator.getInstance(context.getApplicationContext());
        VolleyCallback callback = new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response){
                loadupMyTripFragmenet();
            }

            @Override
            public void onError(String result){
                Toast.makeText(context, "Sorry we faced some issue,\n Please reload the page", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Saved login verification not successful");
                Toast.makeText(context, "Please try again", Toast.LENGTH_LONG).show();
            }

        };

        try {
            profileJsonObject = new JSONObject((profileJson));
            communicator.volleyPut(url,profileJsonObject,callback);
        } catch (JSONException e) {
            Log.d(TAG, "Error making login JSONObject");
            Log.d(TAG, "JSONException: " + e.toString());
            e.printStackTrace();
        }
    }
}
