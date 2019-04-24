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
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

public class WeightFragment extends Fragment {

    private Button buttonWeight;
    private NumberPicker numberPickerWeight;
    private NumberPicker numberPickerFeet;
    private NumberPicker numberPickerInches;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weight, container, false);
        numberPickerWeight = view.findViewById(R.id.numberpicker_weight);
        numberPickerFeet = view.findViewById(R.id.numberpicker_feet);
        numberPickerInches = view.findViewById(R.id.numberpicker_inches);
        buttonWeight = view.findViewById(R.id.button_Weight_Change);

        numberPickerWeight.setMaxValue(600);
        numberPickerWeight.setMinValue(20);
        numberPickerWeight.setWrapSelectorWheel(true);
        numberPickerWeight.setValue(160);

        numberPickerFeet.setMaxValue(9);
        numberPickerFeet.setMinValue(0);
        numberPickerFeet.setWrapSelectorWheel(true);
        numberPickerFeet.setValue(5);

        numberPickerInches.setMaxValue(11);
        numberPickerInches.setMinValue(0);
        numberPickerInches.setWrapSelectorWheel(true);
        numberPickerInches.setValue(6);

        buttonWeight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                int inches = numberPickerInches.getValue();
                int feet = numberPickerFeet.getValue();
                int weight = numberPickerWeight.getValue();

                if (inches == 0 && feet == 0) {
                    Toast.makeText(getActivity(), "Please input a valid height.", Toast.LENGTH_SHORT).show();
                } else {
                    int height = (feet * 12) + inches;

                    Bundle bundle = new Bundle();
                    bundle.putInt("height", height);
                    bundle.putInt("weight", weight);

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