package com.example.tagalong;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

public class Profile_Fragment extends Fragment {

    TextView name, username, age, email, interests, carCap, registeredAs, gender;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        Bundle inputBundle = getArguments();

        Profile userProfile = (Profile) inputBundle.getSerializable("profile");

        name = (TextView) view.findViewById(R.id.name);
        username = (TextView) view.findViewById(R.id.userName);
        email = (TextView) view.findViewById(R.id.email);
        age = (TextView) view.findViewById(R.id.age);
        interests = (TextView) view.findViewById(R.id.inter);
        carCap = (TextView) view.findViewById(R.id.carCapacity);
        registeredAs = (TextView) view.findViewById(R.id.registeredAs);
        gender = (TextView) view.findViewById(R.id.gender);

        if (userProfile != null){
            name.setText("Name : " + userProfile.getFirstName()+ " " + userProfile.getLastName());
            username.setText("User Name: " + userProfile.getUserName());
            email.setText("Email address :" + userProfile.getEmail());
            age.setText("Age :" + userProfile.getAge());

            interests.setText("Interests: " + userProfile.getInterest());
            if(userProfile.getDriver()){
                registeredAs.setText(" Registered as : Driver");
                carCap.setText("Car Capacity: " + userProfile.getCarCapacity());
            } else {
                registeredAs.setText("Registered as Rider");
            }
        }

        return view;
    }
}
