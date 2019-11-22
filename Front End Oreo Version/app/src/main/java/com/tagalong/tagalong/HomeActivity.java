package com.tagalong.tagalong;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import java.io.FileOutputStream;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.facebook.login.LoginManager;

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
                removeSavedFiles();
                Intent intent2 = new Intent(context, MainActivity.class);
                startActivity(intent2);
                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                HomeActivity.this.finish();
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
}
