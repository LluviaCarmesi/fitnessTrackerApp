package com.team3.fitnesstrackerapp3;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class WeeklyProgressFragment extends Fragment { //Add progress bars for each day
    private static String STEPS_KEY = "Steps";

    private TextView textViewDay1;
    private TextView textViewDay2;
    private TextView textViewDay3;
    private TextView textViewDay4;
    private TextView textViewDay5;
    private TextView textViewDay6;
    private TextView textViewDay7;
    private ProgressBar progressBarDay1;
    private ProgressBar progressBarDay2;
    private ProgressBar progressBarDay3;
    private ProgressBar progressBarDay4;
    private ProgressBar progressBarDay5;
    private ProgressBar progressBarDay6;
    private ProgressBar progressBarDay7;
    private int dailyGoal = 0;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference day1 = db.collection("Weekly Progress").document("Day 1");
    DocumentReference day2 = db.collection("Weekly Progress").document("Day 2");
    DocumentReference day3 = db.collection("Weekly Progress").document("Day 3");
    DocumentReference day4 = db.collection("Weekly Progress").document("Day 4");
    DocumentReference day5 = db.collection("Weekly Progress").document("Day 5");
    DocumentReference day6 = db.collection("Weekly Progress").document("Day 6");
    DocumentReference day7 = db.collection("Weekly Progress").document("Day 7"); //Sets the path to get values from the database

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weekly_progress, container, false);

        Bundle bundle = getArguments(); //Obtains values from Home fragment
        dailyGoal = bundle.getInt("dailyGoalProgress");

        textViewDay1 = view.findViewById(R.id.textView_monday_steps);
        textViewDay2 = view.findViewById(R.id.textView_tuesday_steps);
        textViewDay3 = view.findViewById(R.id.textView_wednesdau_steps);
        textViewDay4 = view.findViewById(R.id.textView_thursday_steps);
        textViewDay5 = view.findViewById(R.id.textView_friday_steps);
        textViewDay6 = view.findViewById(R.id.textView_saturday_steps);
        textViewDay7 = view.findViewById(R.id.textView_sunday_steps);
        progressBarDay1 = view.findViewById(R.id.progressBar_day_1);
        progressBarDay2 = view.findViewById(R.id.progressBar_day_2);
        progressBarDay3 = view.findViewById(R.id.progressBar_day_3);
        progressBarDay4 = view.findViewById(R.id.progressBar_day_4);
        progressBarDay5 = view.findViewById(R.id.progressBar_day_5);
        progressBarDay6 = view.findViewById(R.id.progressBar_day_6);
        progressBarDay7 = view.findViewById(R.id.progressBar_day_7); //Initializes items from the xml file
        progressBarDay1.setMax(dailyGoal);
        progressBarDay2.setMax(dailyGoal);
        progressBarDay3.setMax(dailyGoal);
        progressBarDay4.setMax(dailyGoal);
        progressBarDay5.setMax(dailyGoal);
        progressBarDay6.setMax(dailyGoal);
        progressBarDay7.setMax(dailyGoal);

        day1.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() { //Gets values from database in their separate documents
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String day1 = documentSnapshot.getString(STEPS_KEY);
                        int dayProgress = Integer.parseInt(day1);

                        textViewDay1.setText(day1);
                        progressBarDay1.setProgress(dayProgress);
                    }
                });

        day2.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String day2 = documentSnapshot.getString(STEPS_KEY);
                        int dayProgress = Integer.parseInt(day2);

                        textViewDay2.setText(day2);
                        progressBarDay2.setProgress(dayProgress);
                    }
                });

        day3.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String day3 = documentSnapshot.getString(STEPS_KEY);
                        int dayProgress = Integer.parseInt(day3);

                        textViewDay3.setText(day3);
                        progressBarDay3.setProgress(dayProgress);
                    }
                });

        day4.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String day4 = documentSnapshot.getString(STEPS_KEY);
                        int dayProgress = Integer.parseInt(day4);

                        textViewDay4.setText(day4);
                        progressBarDay4.setProgress(dayProgress);
                    }
                });

        day5.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String day5 = documentSnapshot.getString(STEPS_KEY);
                        int dayProgress = Integer.parseInt(day5);

                        textViewDay5.setText(day5);
                        progressBarDay5.setProgress(dayProgress);
                    }
                });

        day6.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String day6 = documentSnapshot.getString(STEPS_KEY);
                        int dayProgress = Integer.parseInt(day6);

                        textViewDay6.setText(day6);
                        progressBarDay6.setProgress(dayProgress);
                    }
                });

        day7.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String day7 = documentSnapshot.getString(STEPS_KEY);
                        int dayProgress = Integer.parseInt(day7);

                        textViewDay7.setText(day7);
                        progressBarDay7.setProgress(dayProgress);
                    }
                });

        return view;
}
}
