package com.tagalong.tagalong.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.tagalong.tagalong.models.Profile;
import com.tagalong.tagalong.R;

public class ViewUserAlongProfileActivity extends AppCompatActivity {

    private TextView name;
    private TextView username;
    private TextView age;
    private TextView email;
    private TextView interests;
    private TextView carCap;
    private TextView registeredAs;
    private TextView gender;
    private Context context;

    private Profile userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_useralong_profile);
        context = getApplicationContext();
        userProfile = (Profile) getIntent().getSerializableExtra("userAlongProfile");

        name = (TextView) findViewById(R.id.userAlongName);
        username = (TextView) findViewById(R.id.userAlongUsername);
        email = (TextView) findViewById(R.id.userAlongEmail);
        age = (TextView) findViewById(R.id.userAlongAge);
        interests = (TextView) findViewById(R.id.userAlongInterests);
        carCap = (TextView) findViewById(R.id.userAlongCarCapacity);
        registeredAs = (TextView) findViewById(R.id.userAlongRegisteredAs);
        gender = (TextView) findViewById(R.id.userAlongGender);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (userProfile != null) {
            name.setText(Html.fromHtml("<b>Name: </b>" + userProfile.getFirstName() + " " + userProfile.getLastName()));
            username.setText(Html.fromHtml("<b>Username: </b>" + userProfile.getUsername()));
            email.setText(Html.fromHtml("<b>Email address: </b>" + userProfile.getEmail()));
            age.setText(Html.fromHtml("<b>Age: </b>" + userProfile.getAge()));
            gender.setText(Html.fromHtml("<b>Gender: </b>" + userProfile.getGender()));
            if (userProfile.getDriver()) {
                registeredAs.setText(Html.fromHtml("<b>Registered as:</b> Driver"));
                carCap.setText(Html.fromHtml("<b>Car Capacity: </b>" + userProfile.getCarCapacity()));
            } else {
                registeredAs.setText(Html.fromHtml("<b>Registered as: </b>Rider"));
                carCap.setText(Html.fromHtml("<b>Car Capacity: </b>" + "Not Applicable"));
            }
            interests.setText(Html.fromHtml("<b>Preference:</b><br>" +
                    "&emsp&emsp&emsp&emsp&emsp<b>1) Music: </b>" + (userProfile.getInterests()[0] + 1) + "/5<br>" +
                    "&emsp&emsp&emsp&emsp&emsp<b>2) Chatting: </b>" + (userProfile.getInterests()[1] + 1) + "/5<br>" +
                    "&emsp&emsp&emsp&emsp&emsp<b>3) Speed: </b>" + (userProfile.getInterests()[2] + 1) + "/5<br>" +
                    "&emsp&emsp&emsp&emsp&emsp<b>4) Fragrance: </b>" + (userProfile.getInterests()[3] + 1) + "/5<br>" +
                    "&emsp&emsp&emsp&emsp&emsp<b>5) Smoking: </b>" + (userProfile.getInterests()[4] + 1) + "/5"));
        }

    }
}
