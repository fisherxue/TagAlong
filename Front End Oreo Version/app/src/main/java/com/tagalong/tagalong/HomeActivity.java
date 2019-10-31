package com.tagalong.tagalong;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
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
        System.out.println(userProfile.getUserID());
        BottomNavigationView btv = findViewById(R.id.bottom_navigation);
        btv.setOnNavigationItemSelectedListener(lister);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
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
                Intent intent2 = new Intent(context, MainActivity.class);
                startActivity(intent2);
                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                HomeActivity.this.finish();
                break;
            default :
                super.onOptionsItemSelected(item);
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
                            break;

                        case R.id.nav_maps:
                            Intent intent = new Intent(context, MapsFragment.class);
                            intent.putExtra("profile", userProfile);
                            startActivity(intent);
                            break;

                        case R.id.nav_chat:
                            frag = new ChatFragment();
                            break;

                        /*
                        case R.id.nav_profile:
                            frag = new Profile_Fragment();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("profile", userProfile);
                            frag.setArguments(bundle);
                            break;*/
                        default:
                            return true;
                    }

                    if (frag != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, frag).commit();
                    }
                    return true;
                }
            };
}
