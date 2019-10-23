package com.example.tagalong;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class Signup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        if (getIntent().getSerializableExtra("profile") != null) {
            Profile userProfile = (Profile) getIntent().getSerializableExtra("profile");
            Bundle bundle = new Bundle();
            bundle.putSerializable("profile",userProfile);

            FragmentManager fragmentManager = this.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            Signup2_Fragment frag = new Signup2_Fragment();
            frag.setArguments(bundle);

            fragmentTransaction.replace(R.id.fragment_signup_container, frag);
            fragmentTransaction.commit();
        }
        else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_signup_container, new Signup1_Fragment()).commit();
        }
        //BottomNavigationView btv = findViewById(R.id.menu_signup);
        //btv.setOnNavigationItemReselectedListener(lister);


    }

    /*
    private BottomNavigationView.OnNavigationItemReselectedListener lister =
            new BottomNavigationView.OnNavigationItemReselectedListener() {
                @Override
                public void onNavigationItemReselected(@NonNull MenuItem menuItem) {
                    Fragment frag = null;

                    switch (menuItem.getItemId()){
                        case R.id.signup_one:
                            frag = new Signup1_Fragment();
                            break;

                        case R.id.signup_two:
                            frag = new Signup2_Fragment();
                            break;

                        case R.id.signup_submit:
                            frag = new Signup1_Fragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_signup_container, frag).commit();
                }
            }; */
}