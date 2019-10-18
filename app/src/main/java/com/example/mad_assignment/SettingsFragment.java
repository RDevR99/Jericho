package com.example.mad_assignment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingsFragment extends Fragment {

    // The number picker that picks the number of hours.
    NumberPicker hourPicker;

    // The number picker that picks the number of minutes
    NumberPicker minutePicker;

    // The shared preference object that helps us access the global key and value pairs.
    SharedPreferences sharedPreferences;

    // Turn notifications ON and OFF.
    Switch notificationSwitch;

    Button loginBtn;
    boolean logout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Inflate the Settings Fragment.
        return inflater.inflate(R.layout.fragment_settings, null);

    }



    /*
        The function to enable and disable the notifications.
     */
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

    /*
        Once, the view is created, initialize all the GUI components.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Access the shared preferences to get the key value pairs stored for it.
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

                sharedPreferences.edit().putBoolean("notificationEnabled", notificationSwitch.isChecked()).commit();
                toggleNotificationSettings(notificationSwitch.isChecked());
            }
        });

        loginBtn = getActivity().findViewById(R.id.loginBtn);

        if(!sharedPreferences.getString("account", "").equalsIgnoreCase("") && !sharedPreferences.getString("password", "").equalsIgnoreCase(""))
        {
            loginBtn.setText("Log Out");
            logout = true;
        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!logout) {
                    logout = true;

                    Intent goToLogin = new Intent(getContext(), LoginFunction.class);
                    startActivity(goToLogin);
                }
                else
                {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("account", "");
                    editor.putString("password", "");
                    loginBtn.setText("Login");
                    logout = false;
                    editor.commit();
                }
            }
        });

    }

    // Respond to change in the values of the hour and the minute picker.
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
