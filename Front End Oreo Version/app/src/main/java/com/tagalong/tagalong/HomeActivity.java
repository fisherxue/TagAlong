package com.tagalong.tagalong;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
//import android.security.keystore.KeyGenParameterSpec;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

//import java.io.BufferedWriter;
//import java.io.File;
import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.OutputStreamWriter;
//import java.security.GeneralSecurityException;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
//import androidx.security.crypto.EncryptedFile;
//import androidx.security.crypto.MasterKeys;

public class HomeActivity extends AppCompatActivity {
    private final String TAG = "HomeActivity";
    private Context context;
    private Profile userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);

        context = this;
        userProfile = (Profile) getIntent().getSerializableExtra("profile") ;
        System.out.println(userProfile.getUserID());
        BottomNavigationView btv = findViewById(R.id.bottom_navigation);
        btv.setOnNavigationItemSelectedListener(lister);

        Fragment frag = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("profile", userProfile);
        frag.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, frag).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile :
                Intent intent = new Intent(context, ViewProfileActivity.class);
                intent.putExtra("profile", userProfile);
                startActivity(intent);
                break;
            case R.id.logout :
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
                    Fragment frag = null;

                    switch (menuItem.getItemId()){

                        case R.id.nav_home:
                            frag = new HomeFragment();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("profile", userProfile);
                            frag.setArguments(bundle);
                            break;

                        case R.id.nav_maps:
                            Intent intent = new Intent(context, MapsFragment.class);
                            intent.putExtra("profile", userProfile);
                            startActivity(intent);
                            break;

                        case R.id.nav_chat:
                            frag = new ChatFragment();
                            break;

                        default:
                            break;
                    }

                    if (frag != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, frag).commit();
                    }
                    return true;
                }
            };

    private void removeSavedFiles(){
        //context.deleteFile("Saved_TripList.txt");
        context.deleteFile("Saved_Profile.txt");
        userProfile = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (userProfile != null) {
            /*
            KeyGenParameterSpec keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC;
            String masterKeyAlias = null;

            try {
                masterKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec);
                Log.d(TAG, "key: " + masterKeyAlias);
            } catch (GeneralSecurityException e) {
                Log.d(TAG, "Error : creating key for encryption");
                Log.d(TAG, "Error (General Security Exception): " + e.toString());
                e.printStackTrace();
            } catch (IOException e) {
                Log.d(TAG, "Error : creating key for encryption");
                Log.d(TAG, "Error (IOException): " + e.toString());
                e.printStackTrace();
            }

            String fileToWrite = "Saved_Profile.txt";
            try {
                EncryptedFile encryptedFile = new EncryptedFile.Builder(
                        new File(context.getFilesDir(), fileToWrite),
                        context,
                        masterKeyAlias,
                        EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
                ).build();

                // Write to a file.
                Gson gson = new Gson();
                String profileJson = gson.toJson(userProfile);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                        encryptedFile.openFileOutput()));
                writer.write(profileJson);
            } catch (GeneralSecurityException e) {
                Log.d(TAG, "Error : creating key for encryption");
                Log.d(TAG, "Error (General Security Exception): " + e.toString());
                e.printStackTrace();
            } catch (IOException e) {
                Log.d(TAG, "Error : creating key for encryption");
                Log.d(TAG, "Error (IOException): " + e.toString());
                e.printStackTrace();
            }
             */

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
                Log.d(TAG, "Error : Exception Writing file");
                Log.d(TAG, "Error (Exception): " + e.toString());
                e.printStackTrace();
            } finally {
                Log.d(TAG, "Saved profile in Saved_Profile.txt");
            }

        }

    }
}
