package com.example.myapplication.Fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.SearchView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.myapplication.R;

public class CreateEventFragment extends Fragment {
    private TimePicker picker;
    private DatePicker datePicker;
    private EditText etEventDescription;
    private EditText etEventTitle;
    private Button btnCreateEvent;
    private SearchView sVAddFriends;
    private NumberPicker numberPicker;

    private int numPics = 2;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_create_event, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
       super.onViewCreated(view, savedInstanceState);

        picker = view.findViewById(R.id.timePicker1);
        datePicker = view.findViewById(R.id.datePicker);
        etEventDescription = view.findViewById(R.id.etDescription);
        etEventTitle = view.findViewById(R.id.etEventTitle);
        btnCreateEvent = view.findViewById(R.id.btnCreateEvent);
        sVAddFriends = view.findViewById(R.id.svAddFriends);
        numberPicker = view.findViewById(R.id.numberPicker);

        //assigning the exact time for the event, to display later and add to event object
        picker.setIs24HourView(true);
        int hour, minute;
        String am_pm;
        hour = picker.getCurrentHour();
        minute = picker.getCurrentMinute();
        if(hour > 12) {
            am_pm = "PM";
            hour = hour - 12;
        }
        else {
            am_pm="AM";
        }

        //adjust the number of pictures a user can choose
        numberPicker.setMinValue(2);
        numberPicker.setMaxValue(20);

        numberPicker.setOnValueChangedListener(onValueChangeListener);
    }

    NumberPicker.OnValueChangeListener onValueChangeListener = new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                    //Toast.makeText(MainActivity.this, "selected number "+numberPicker.getValue(), Toast.LENGTH_SHORT);
                    numPics = numberPicker.getValue();
                }
            };
}
