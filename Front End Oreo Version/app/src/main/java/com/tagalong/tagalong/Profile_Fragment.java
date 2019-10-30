package com.tagalong.tagalong;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Profile_Fragment extends Fragment {

    TextView name, username, age, email, interests, carCap, registeredAs, gender;
    Button edit, logout;
    Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        Bundle inputBundle = getArguments();
        context = getActivity();
        Profile userProfile = (Profile) inputBundle.getSerializable("profile");

        name = (TextView) view.findViewById(R.id.name);
        username = (TextView) view.findViewById(R.id.userName);
        email = (TextView) view.findViewById(R.id.email);
        age = (TextView) view.findViewById(R.id.age);
        interests = (TextView) view.findViewById(R.id.inter);
        carCap = (TextView) view.findViewById(R.id.carCapacity);
        registeredAs = (TextView) view.findViewById(R.id.registeredAs);
        gender = (TextView) view.findViewById(R.id.gender);
        edit = (Button) view.findViewById(R.id.edit);
        logout = (Button) view.findViewById(R.id.logout);


        if (userProfile != null){
            name.setText("Name : " + userProfile.getFirstName()+ " " + userProfile.getLastName());
            username.setText("User Name: " + userProfile.getUserName());
            email.setText("Email address :" + userProfile.getEmail());
            age.setText("Age :" + userProfile.getAge());

            //interests.setText("Interests: " + userProfile.getInterest());
            if(userProfile.getDriver()){
                registeredAs.setText(" Registered as : Driver");
                carCap.setText("Car Capacity: " + userProfile.getCarCapacity());
            } else {
                registeredAs.setText("Registered as Rider");
            }
        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });


        return view;
    }
}
