package com.tagalong.tagalong;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class HomeActivity extends AppCompatActivity {
    private Context context;
    private Profile userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);

        context = this;
        userProfile = (Profile) getIntent().getSerializableExtra("profile") ;

        BottomNavigationView btv = findViewById(R.id.bottom_navigation);
        btv.setOnNavigationItemSelectedListener(lister);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Home_Fragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener lister =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment frag = null;

                    switch (menuItem.getItemId()){
                        case R.id.nav_home:
                            frag = new Home_Fragment();
                            break;

                        case R.id.nav_maps:
                            Intent intent = new Intent(context, Chat_Fragment.class);
                            intent.putExtra("profile", userProfile);
                            startActivity(intent);
                            break;

                        case R.id.nav_chat:
                            frag = new Chat_Fragment();
                            break;

                        case R.id.nav_profile:
                            frag = new Profile_Fragment();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("profile", userProfile);
                            frag.setArguments(bundle);
                            break;
                    }

                    if (frag != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, frag).commit();
                    }
                    return true;
                }
            };
}
