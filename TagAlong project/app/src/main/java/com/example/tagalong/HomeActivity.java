package com.example.tagalong;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);

        BottomNavigationView btv = findViewById(R.id.bottom_navigation);
        btv.setOnNavigationItemReselectedListener(lister);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Home_Fragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemReselectedListener lister =
            new BottomNavigationView.OnNavigationItemReselectedListener() {
                @Override
                public void onNavigationItemReselected(@NonNull MenuItem menuItem) {
                    Fragment frag = null;

                    switch (menuItem.getItemId()){
                        case R.id.nav_home:
                            frag = new Home_Fragment();
                            break;

                        case R.id.nav_maps:
                            frag = new Maps_Fragment();
                            break;

                        case R.id.nav_chat:
                            frag = new Chat_Fragment();
                            break;

                        case R.id.nav_profile:
                            frag = new Profile_Fragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, frag).commit();
                }
            };
}
