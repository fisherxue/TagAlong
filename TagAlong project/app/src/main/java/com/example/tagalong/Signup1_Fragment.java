package com.example.tagalong;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Signup1_Fragment extends Fragment {
    private EditText fn, ln, un, pass;
    private Button nxt;
    private Context context;
    private boolean allSet;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_1, container, false);
        context = getActivity();

        final Profile newUserProfile = new Profile();
        fn = (EditText) view.findViewById(R.id.firstname);
        ln = (EditText) view.findViewById(R.id.lastname);
        un = (EditText) view.findViewById(R.id.username);
        pass = (EditText) view.findViewById(R.id.password);
        nxt = (Button) view.findViewById(R.id.nextbutton);

        nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allSet = true;

                if (!fn.getText().toString().isEmpty()) {
                    newUserProfile.setFirstName(fn.getText().toString());
                } else {
                    Toast.makeText(context, "Please Enter First Name", Toast.LENGTH_LONG).show();
                    allSet = false;
                }

                if (!ln.getText().toString().isEmpty()){
                    newUserProfile.setLastName(ln.getText().toString());
                } else {
                    Toast.makeText(context, "Please Enter Last Name", Toast.LENGTH_LONG).show();
                    allSet = false;
                }

                if (!un.getText().toString().isEmpty()){
                    newUserProfile.setUserName(un.getText().toString());
                } else {
                    Toast.makeText(context, "Please Enter Username", Toast.LENGTH_LONG).show();
                    allSet = false;
                }

                if (!pass.getText().toString().isEmpty()){
                    newUserProfile.setPassword(pass.getText().toString());
                } else {
                    Toast.makeText(context, "Please Enter Password", Toast.LENGTH_LONG).show();
                    allSet = false;
                }

                if (allSet) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("profile",newUserProfile);

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    Signup2_Fragment frag = new Signup2_Fragment();
                    frag.setArguments(bundle);

                    fragmentTransaction.replace(R.id.fragment_signup_container, frag);
                    fragmentTransaction.commit();

                }

            }
        });

        return view;
    }
}

