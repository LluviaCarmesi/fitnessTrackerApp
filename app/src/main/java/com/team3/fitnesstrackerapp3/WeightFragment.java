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

public class WeightFragment extends Fragment {

    private Button buttonWeight;
    private NumberPicker numberPickerWeight;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weight, container, false);
        numberPickerWeight = view.findViewById(R.id.numberpicker_weight);
        buttonWeight = view.findViewById(R.id.button_Weight_Change);

        numberPickerWeight.setMaxValue(600);
        numberPickerWeight.setMinValue(100);
        numberPickerWeight.setWrapSelectorWheel(true);

        buttonWeight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                Bundle bundle = new Bundle();

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                HomeFragment homeFragment = new HomeFragment();
                homeFragment.setArguments(bundle);

                fragmentTransaction.replace(R.id.fragment_container, homeFragment);
                fragmentTransaction.commit();
            }
        });

        return view;
    }

}