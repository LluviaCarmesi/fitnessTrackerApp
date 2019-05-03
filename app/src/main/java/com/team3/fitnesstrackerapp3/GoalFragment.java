package com.team3.fitnesstrackerapp3;

import android.graphics.Color;
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
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class GoalFragment extends Fragment {
    private Button buttonDailyGoal;
    private static SeekBar seekBarGoal;
    private static TextView textViewGoal;
    private int progress_value = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goal, container, false);

        buttonDailyGoal = view.findViewById(R.id.button_goal_change);

        seekBarGoal = view.findViewById(R.id.seek_bar_goal);
        textViewGoal = view.findViewById(R.id.text_view_goal);

        seekBarGoal.setMax(80);
        seekBarGoal.setProgress(10);

        seekbar();

        buttonDailyGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (progress_value <= 6) {
                    Toast.makeText(getActivity(), "Please try to motivate yourself", Toast.LENGTH_SHORT).show();
                } else {

                    progress_value = progress_value * 500;
                    Bundle bundle = new Bundle();
                    bundle.putInt("dailyGoal", progress_value);

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    HomeFragment homeFragment = new HomeFragment();
                    homeFragment.setArguments(bundle);

                    fragmentTransaction.replace(R.id.fragment_container, homeFragment);
                    fragmentTransaction.commit();
                }
            }
        });
        return view;
    }

    public void seekbar() {
        textViewGoal.setText("Goal: 5000 Steps");

        seekBarGoal.setOnSeekBarChangeListener(

                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                        progress_value = progress;

                        textViewGoal.setText("Goal: " + (progress * 500) + " Steps");

                        if(progress_value >= 6) {
                            textViewGoal.setTextColor(Color.parseColor("#00CC00"));
                        }
                        else {
                            textViewGoal.setTextColor(Color.parseColor("#CC0000"));
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );
    }
}
