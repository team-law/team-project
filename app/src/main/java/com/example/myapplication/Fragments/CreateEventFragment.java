package com.example.myapplication.Fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

// import com.example.myapplication.Activities.ContactsListActivity;
import com.example.myapplication.Activities.ContactsListActivity;
import com.example.myapplication.Activities.HomeActivity;
import com.example.myapplication.HorizontalNumberPicker;
import com.example.myapplication.Models.Event;
import com.example.myapplication.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class CreateEventFragment extends Fragment {


    private static final int PERMISSION_REQUEST_CODE = 1;

    private final String TAG = "CreateEventFragment";
    private TextView tvDatePicker;
    private TextView tvTimePicker;
    private EditText etEventDescription;
    private EditText etEventTitle;
    private EditText etLocation;
    private Button btnCreateEvent;


    Calendar cDate;
    DatePickerDialog datepickerdialog;
    private int iDay;
    private int iMonth;
    private int iYear;

    Calendar cTime;
    TimePickerDialog timePickerDialog;
    private int iHour;
    private int iMinute;
    public String am_pm;



    private HorizontalNumberPicker np_channel_nr;

    public int numPics = 1;
    public List<String> invited;

    //Firebase things
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    FirebaseUser user;
    //might need to be using the FirebaseApp ones?

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

        tvDatePicker = view.findViewById(R.id.tvDatePicker);
        etEventDescription = view.findViewById(R.id.etDescription);
        etEventTitle = view.findViewById(R.id.etEventTitle);
        etLocation = view.findViewById(R.id.etLocation);
        btnCreateEvent = view.findViewById(R.id.btnCreateEvent);
        np_channel_nr = view.findViewById(R.id.np_channel_nr);
        tvTimePicker = view.findViewById(R.id.tvTimePicker);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) { //user is signed in

                } else {

                }
            }
        };

        // set number picker
        np_channel_nr.setMax(5);

        np_channel_nr.setMin(1);

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                Log.d(TAG, "Value is: " + map);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        tvDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cDate = Calendar.getInstance();
                int day = cDate.get(Calendar.DAY_OF_MONTH);
                int month = cDate.get(Calendar.MONTH);
                int year = cDate.get(Calendar.YEAR);

                datepickerdialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        tvDatePicker.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                        iDay = dayOfMonth;
                        iMonth = month;
                        iYear = year;
                    }
                }, day, month, year);
                datepickerdialog.show();
            }
        });

        tvTimePicker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cTime = Calendar.getInstance();
                int hour = cTime.get(Calendar.HOUR_OF_DAY);
                int minute = cTime.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                       int hour;
                        if (selectedHour > 12) {
                            am_pm = "PM";
                            hour = selectedHour - 12;
                        } else {
                            am_pm = "AM";
                            hour = selectedHour;
                        }
                        tvTimePicker.setText( hour + ":" + selectedMinute);
                        iHour = selectedHour;
                        iMinute = selectedMinute;
                    }
                }, hour, minute, false);
                timePickerDialog.show();
            }
        });


//        //dealing with searchview
//        sVAddFriends.setQueryHint("Search For Friends");
//        CharSequence query = sVAddFriends.getQuery();

//        // perform set on query text listener event
//        sVAddFriends.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                // do something on text submit
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                // do something when text changes
//                String text = newText;
//                //adapter.filter(text);
//                return false;
//            }
//        });

        //assigning the exact time for the event, to display later and add to event object
//        picker.setIs24HourView(false);
//        hour = picker.getCurrentHour();
//        minute = picker.getCurrentMinute();
//        if (hour > 12) {
//            am_pm = "PM";
//            hour = hour - 12;
//        } else {
//            am_pm = "AM";
//        }

        // TODO -- add logic so you can't choose a day in the past

        btnCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // read the index key
                //Event event = new Event();
                String mGroupId = myRef.push().getKey();

                String title = etEventTitle.getText().toString();
                String description = etEventDescription.getText().toString();
                String date = iMonth + "/" +iDay + "/" + iYear;
                String time = String.valueOf(iHour) + ":" + iMinute + " "+ am_pm;
                String location = etLocation.getText().toString();
                invited = new ArrayList<>(); //should be retrieved from the search view
                Map<String, Boolean> attending = new HashMap<>(1);
                attending.put(user.getUid(), true);
                Map<String, Boolean> pics = new HashMap<>(0);
                String accessCode = getCode();
                numPics = np_channel_nr.getValue();

                DatabaseReference eventsRef = myRef.child("Events");



                Event event = new Event(user.getUid(), user.getDisplayName(), title, time, date, description, location, numPics, invited, attending, pics, accessCode);
                eventsRef.child(accessCode).setValue(event); //creates the event in firebase

                //add event to host list of events
                DatabaseReference userEventRef = myRef.child("UserNodes").child(user.getUid()).child("eventsAttending");
                userEventRef.child(accessCode).setValue(true); //adds the event to the user's list of events, marks true as them being the host

                Toast.makeText(getActivity(), "Event created successfully!", Toast.LENGTH_SHORT).show();
                if (checkPermission()) {
                    Intent intent = new Intent(getActivity(), ContactsListActivity.class);
                    intent.putExtra(Event.class.getSimpleName(), Parcels.wrap(event));
                    intent.putExtra("hostName", user.getDisplayName());
                    startActivity(intent);
                } else {
                    requestPermission();
                }

                //reset all the fields in the create fragment
                etEventTitle.setText("");
                etEventDescription.setText("");
                etLocation.setText("");
               // numberPicker.setValue(2);

            }
        });

        // temporary button to send a text message
//        btnSendInvite = view.findViewById(R.id.btnSendInvite);
//        btnSendInvite.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                if (checkPermission()) {
//                    Log.e("permission", "Permission already granted.");
//                } else {
//                    requestPermission();
//                }
//                // send sms invite to users
//                sendInvite();
//
//            }
//        });
    }

    private boolean checkPermission() {
        int smsResult = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS);
        int contactsResult = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS);
        return smsResult == PackageManager.PERMISSION_GRANTED && contactsResult == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "SMS sent.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "SMS failed, please try again.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public String getCode() {
        //generate randomized access code and check to see if it already exists
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder code = new StringBuilder();
        Random rand = new Random();
        while (code.length() < 7) { // length of the random string.
            int index = rand.nextInt(characters.length());
            code.append(characters.charAt(index));
        }
        final String finalCode = code.toString();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.child("Events").getChildren()) {
                    if ((snapshot.getKey()).equals(finalCode)) {
                        getCode();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        return finalCode;
    }

//    NumberPicker.OnValueChangeListener onValueChangeListener = new NumberPicker.OnValueChangeListener() {
//        @Override
//        public void onValueChange(NumberPicker numberPicker, int i, int i1) {
//            numPics = numberPicker.getValue();
//        }
//    };

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
