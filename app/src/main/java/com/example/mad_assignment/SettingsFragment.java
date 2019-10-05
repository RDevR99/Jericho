package com.example.mad_assignment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {

    NumberPicker hourPicker;
    NumberPicker minutePicker;
    SharedPreferences sharedPreferences;

    Switch notificationSwitch;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_settings, null);

    }

    public void toggleNotificationSettings(Boolean enabled)
    {
        if(!enabled)
        {
            hourPicker.setEnabled(false);
            minutePicker.setEnabled(false);
            ((MainActivity)getActivity()).cancelAlarms();
        }
        else
        {
            hourPicker.setEnabled(true);
            minutePicker.setEnabled(true);
            ((MainActivity)getActivity()).setAlarms();
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences("MyPreferences", 0);
        notificationSwitch = view.findViewById(R.id.notificationSwitch);

        hourPicker = getActivity().findViewById(R.id.hourPicker);
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(24);
        hourPicker.setValue(sharedPreferences.getInt("notifyMeBeforeHours", 0));
        hourPicker.setOnValueChangedListener(onValueChangeListener);

        minutePicker = getActivity().findViewById(R.id.minutePicker);
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(60);
        minutePicker.setValue(sharedPreferences.getInt("notifyMeBeforeMinutes",0));
        minutePicker.setOnValueChangedListener(onValueChangeListener);

        notificationSwitch.setChecked(sharedPreferences.getBoolean("notificationEnabled", true));
        toggleNotificationSettings(notificationSwitch.isChecked());
        notificationSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //notificationSwitch.setChecked(!notificationSwitch.isChecked());
                sharedPreferences.edit().putBoolean("notificationEnabled", notificationSwitch.isChecked()).commit();
                toggleNotificationSettings(notificationSwitch.isChecked());
            }
        });

    }


    NumberPicker.OnValueChangeListener onValueChangeListener =

            new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {

                    switch (numberPicker.getId()) {

                        case R.id.hourPicker:
                            ((MainActivity) getActivity()).changeNotificatonHours(numberPicker.getValue());

                            break;
                        case R.id.minutePicker:
                            ((MainActivity) getActivity()).changeNotificatonMinutes(numberPicker.getValue());
                            break;

                    }

                }

            };
}
