package com.tagalong.tagalong;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ViewProfileActivity extends AppCompatActivity {

    private TextView name, username, age, email, interests, carCap, registeredAs, gender;
    private Button edit, logout;
    private Context context;

    private Profile userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        context = getApplicationContext();
        userProfile = (Profile) getIntent().getSerializableExtra("profile") ;

        name = (TextView) findViewById(R.id.name);
        username = (TextView) findViewById(R.id.userName);
        email = (TextView) findViewById(R.id.email);
        age = (TextView) findViewById(R.id.age);
        interests = (TextView) findViewById(R.id.inter);
        carCap = (TextView) findViewById(R.id.carCapacity);
        registeredAs = (TextView) findViewById(R.id.registeredAs);
        gender = (TextView) findViewById(R.id.gender);
        edit = (Button) findViewById(R.id.edit);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (userProfile != null){
            name.setText("Name: " + userProfile.getFirstName()+ " " + userProfile.getLastName());
            username.setText("Username: " + userProfile.getUserName());
            email.setText("Email address: " + userProfile.getEmail());
            age.setText("Age: " + userProfile.getAge());
            gender.setText("Gender: " + userProfile.getGender());
            if(userProfile.getDriver()){
                registeredAs.setText("Registered as: Driver");
                carCap.setText("Car Capacity: " + userProfile.getCarCapacity());
            } else {
                registeredAs.setText("Registered as: Rider");
            }
            interests.setText(" Preference:\n" + "\t\t\tMusic: " + (userProfile.getInterests()[0]+1) +"/5\n" +
                    "\t\t\tChatting: " + (userProfile.getInterests()[1]+1) +"/5\n" +
                    "\t\t\tSpeed: " + (userProfile.getInterests()[2]+1) +"/5\n" +
                    "\t\t\tFragrance: " + (userProfile.getInterests()[3]+1) +"/5\n" +
                    "\t\t\tSmoking: " + (userProfile.getInterests()[4]+1) +"/5\n");
        }

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UpdateProfileActivity.class);
                intent.putExtra("profile", userProfile);
                startActivity(intent);
            }
        });
    }
}
