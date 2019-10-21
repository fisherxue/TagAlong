package com.example.tagalong;

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

public class Signup1_Fragment extends Fragment {
    private EditText fn, ln, un, pass;
    private Button nxt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_1, container, false);

        final Profile newUserProfile = new Profile();
        fn = (EditText) view.findViewById(R.id.firstname);
        ln = (EditText) view.findViewById(R.id.lastname);
        un = (EditText) view.findViewById(R.id.username);
        pass = (EditText) view.findViewById(R.id.password);
        nxt = (Button) view.findViewById(R.id.nextbutton);

        nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newUserProfile.setFirstName(fn.getText().toString());
                newUserProfile.setLastName(ln.getText().toString());
                newUserProfile.setUserName(un.getText().toString());
                newUserProfile.setPassword(pass.getText().toString());

                Bundle bundle = new Bundle();
                bundle.putSerializable("profile",newUserProfile);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                Signup2_Fragment frag = new Signup2_Fragment();
                frag.setArguments(bundle);

                fragmentTransaction.replace(R.id.fragment_signup_container, frag);
                fragmentTransaction.commit();


            }
        });

        return view;
    }
}

