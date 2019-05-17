package com.team3.fitnesstrackerapp3;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.internal.InternalTokenResult;

public class SettingsFragment extends Fragment {
    private TextView textViewProfile;
    private Button buttonLogout;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        textViewProfile = view.findViewById(R.id.text_view_profile);
        buttonLogout = view.findViewById(R.id.button_logout); //Initializes items from xml

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        textViewProfile.setText(firebaseUser.getEmail());

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); //Logs out the user when button is pressed. Doesn't allow user to go back to the main activity

                getActivity().startActivity(intent);
            }
        });
        return view;
    }
}
