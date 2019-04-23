package com.team3.fitnesstrackerapp3;

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
import android.widget.Toast;

public class GoalFragment extends Fragment {
    private NumberPicker numberPickerTenThousands;
    private NumberPicker numberPickerThousands;
    private NumberPicker numberPickerHundreds;
    private NumberPicker numberPickerTens;
    private NumberPicker numberPickerOnes;
    private Button buttonDailyGoal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goal, container, false);

        numberPickerTenThousands = view.findViewById(R.id.numberpicker_ten_thousands);
        numberPickerThousands = view.findViewById(R.id.numberpicker_thousands);
        numberPickerHundreds = view.findViewById(R.id.numberpicker_hundreds);
        numberPickerTens = view.findViewById(R.id.numberpicker_tens);
        numberPickerOnes = view.findViewById(R.id.numberpicker_ones);
        buttonDailyGoal = view.findViewById(R.id.button_goal_change);

        numberPickerTenThousands.setMinValue(0);
        numberPickerTenThousands.setMaxValue(4);
        numberPickerTenThousands.setWrapSelectorWheel(true);

        numberPickerThousands.setMinValue(0);
        numberPickerThousands.setMaxValue(9);
        numberPickerThousands.setWrapSelectorWheel(true);
        numberPickerThousands.setValue(5);

        numberPickerHundreds.setMinValue(0);
        numberPickerHundreds.setMaxValue(9);
        numberPickerHundreds.setWrapSelectorWheel(true);

        numberPickerTens.setMinValue(0);
        numberPickerTens.setMaxValue(9);
        numberPickerTens.setWrapSelectorWheel(true);

        numberPickerOnes.setMinValue(0);
        numberPickerOnes.setMaxValue(9);
        numberPickerOnes.setWrapSelectorWheel(true);

        buttonDailyGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tenThousands = numberPickerTenThousands.getValue();
                int thousands = numberPickerThousands.getValue();
                int hundreds = numberPickerHundreds.getValue();
                int tens = numberPickerTens.getValue();
                int ones = numberPickerOnes.getValue();

                String tenThousandsString = Integer.toString(tenThousands);
                String thousandsString = Integer.toString(thousands);
                String hundredsString = Integer.toString(hundreds);
                String tensString = Integer.toString(tens);
                String onesString = Integer.toString(ones);

                if (hundreds <= 9 && (tenThousands == 0 && thousands == 0)) {
                    Toast.makeText(getActivity(), "Please try to motivate yourself", Toast.LENGTH_SHORT).show();
                } else {

                    String dailyGoalString = tenThousandsString + thousandsString + hundredsString +
                            tensString + onesString;

                    int dailyGoal = Integer.parseInt(dailyGoalString);

                    Bundle bundle = new Bundle();
                    bundle.putInt("dailyGoal", dailyGoal);

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
}
