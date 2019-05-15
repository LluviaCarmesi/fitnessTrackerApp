package com.team3.fitnesstrackerapp3;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class WeeklyProgressFragment extends Fragment {
    private static String STEPS_KEY = "Steps";

    private TextView textViewDay1;
    private TextView textViewDay2;
    private TextView textViewDay3;
    private TextView textViewDay4;
    private TextView textViewDay5;
    private TextView textViewDay6;
    private TextView textViewDay7;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference day1 = db.collection("Weekly Progress").document("Day 1");
    DocumentReference day2 = db.collection("Weekly Progress").document("Day 2");
    DocumentReference day3 = db.collection("Weekly Progress").document("Day 3");
    DocumentReference day4 = db.collection("Weekly Progress").document("Day 4");
    DocumentReference day5 = db.collection("Weekly Progress").document("Day 5");
    DocumentReference day6 = db.collection("Weekly Progress").document("Day 6");
    DocumentReference day7 = db.collection("Weekly Progress").document("Day 7");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weekly_progress, container, false);

        textViewDay1 = view.findViewById(R.id.textView_monday_steps);
        textViewDay2 = view.findViewById(R.id.textView_tuesday_steps);
        textViewDay3 = view.findViewById(R.id.textView_wednesdau_steps);
        textViewDay4 = view.findViewById(R.id.textView_thursday_steps);
        textViewDay5 = view.findViewById(R.id.textView_friday_steps);
        textViewDay6 = view.findViewById(R.id.textView_saturday_steps);
        textViewDay7 = view.findViewById(R.id.textView_sunday_steps);

        day1.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String day1 = documentSnapshot.getString(STEPS_KEY);

                        textViewDay1.setText(day1);
                    }
                });

        day2.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String day2 = documentSnapshot.getString(STEPS_KEY);

                        textViewDay2.setText(day2);
                    }
                });

        day3.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String day3 = documentSnapshot.getString(STEPS_KEY);

                        textViewDay3.setText(day3);
                    }
                });

        day4.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String day4 = documentSnapshot.getString(STEPS_KEY);

                        textViewDay4.setText(day4);
                    }
                });

        day5.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String day5 = documentSnapshot.getString(STEPS_KEY);

                        textViewDay5.setText(day5);
                    }
                });

        day6.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String day6 = documentSnapshot.getString(STEPS_KEY);

                        textViewDay6.setText(day6);
                    }
                });

        day7.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String day7 = documentSnapshot.getString(STEPS_KEY);

                        textViewDay7.setText(day7);
                    }
                });

        return view;
}
}
