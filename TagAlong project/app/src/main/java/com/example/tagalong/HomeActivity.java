package com.example.tagalong;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

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
                            Intent intent = new Intent(context, Maps_Fragment.class);
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
