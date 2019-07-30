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
import com.example.myapplication.Activities.CameraActivity;
import com.example.myapplication.Activities.ContactsListActivity;
import com.example.myapplication.Activities.EventDetail;
import com.example.myapplication.Activities.HomeActivity;
import com.example.myapplication.HorizontalNumberPicker;
import com.example.myapplication.Models.Event;
import com.example.myapplication.Models.UserNode;
import com.example.myapplication.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
    private String date = "";

    Calendar cTime;
    TimePickerDialog timePickerDialog;
    private int iHour;
    private int iMinute;
    public String am_pm = "am";
    private String title = "";
    private String description = "";
    private String location = "";
    private String time = "";

    boolean createEventclicked;

    private HorizontalNumberPicker np_channel_nr;

    public int numPics = 1;
    public List<String> invited;

    //Firebase things
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    FirebaseUser user;
    private UserNode currentUser;

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
                mAuth = FirebaseAuth.getInstance();
                user = mAuth.getCurrentUser();
                currentUser = dataSnapshot.child("UserNodes").child(user.getUid()).getValue(UserNode.class);
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

                        tvDatePicker.setText((month + 1) + "/" + dayOfMonth + "/" + year);
                        iDay = dayOfMonth;
                        iMonth = month + 1;
                        iYear = year;
                    }
                }, year, month, day);

                datepickerdialog.show();

            }
        });

        tvTimePicker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cTime = Calendar.getInstance();
                int hour = cTime.get(Calendar.HOUR_OF_DAY);
                final int minute = cTime.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                       int hour = selectedHour;
/*
                        if (selectedHour > 12) {
                            am_pm = "PM";
                            hour = selectedHour - 12;
                        } else {
                            am_pm = "AM";
                            hour = selectedHour;
                        }
*/
                        String mm_precede = "";
                        if (selectedHour >= 12) {
                            am_pm = " PM";
                            if (selectedHour >=13 && selectedHour < 24) {
                                hour -= 12;
                            }
                            else {
                                hour = 12;
                            }
                        } else if (selectedHour == 0) {
                            hour = 12;
                        }
                        if (selectedMinute < 10) {
                            mm_precede = "0";
                        }

                        tvTimePicker.setText(hour + ":" + mm_precede + selectedMinute + " " + am_pm );


                        iHour = selectedHour;
                        iMinute = selectedMinute;

                    }
                }, hour, minute, false);
                timePickerDialog.show();
            }
        });

        btnCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createEventclicked = true;

                // read the index key
                //Event event = new Event();
                String mGroupId = myRef.push().getKey();

                title = etEventTitle.getText().toString();
                description = etEventDescription.getText().toString();
                date = String.valueOf(iYear);
                time = String.valueOf(iHour) + ":" + iMinute + " "+ am_pm;
                location = etLocation.getText().toString();
                invited = new ArrayList<>(); //should be retrieved from the search view
                Map<String, String> attending = new HashMap<>(1);
                attending.put(user.getUid(), user.getUid());
                Map<String, Boolean> pics = new HashMap<>(0);
                String accessCode = getCode();
                numPics = np_channel_nr.getValue();

                //get the date in the right format
                configureDate(iMonth);
                configureDate(iDay);

                DatabaseReference eventsRef = myRef.child("Events");

                if(createEventclicked) {

                    if (checkDateValidity() && checkEventValidity()) {

                        Event event = new Event(user.getUid(), user.getDisplayName(), title, time, date, description, location, numPics, invited, attending, pics, accessCode, false);
                        eventsRef.child(accessCode).setValue(event); //creates the event in firebase

                        //add event to host list of events
                        DatabaseReference userEventRef = myRef.child("UserNodes").child(user.getUid()).child("eventsAttending");
                        userEventRef.child(accessCode).setValue(true); //adds the event to the user's list of events, marks true as them being the host

                        Toast.makeText(getActivity(), "Event created successfully!", Toast.LENGTH_SHORT).show();
                        if (checkPermission()) {
                            Intent intent = new Intent(getActivity(), ContactsListActivity.class);
                            intent.putExtra(Event.class.getSimpleName(), Parcels.wrap(event));
                            intent.putExtra("userCode", currentUser.userCode);
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
                    createEventclicked = false;
                }


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

    //makes sure the date is in the future
    private boolean checkDateValidity() {
        //if year + month + date is less than current - can't create event
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar c = Calendar.getInstance();
        String currentDate = sdf.format(c.getTime());

        if (Integer.parseInt(currentDate) > Integer.parseInt(date)) {
            Toast.makeText(getContext(), "Cannot choose date in the past",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    //makes sure there's no empty required fields
    private boolean checkEventValidity() {
        if (time.equals("") || location.equals("") || description.equals("") || title.equals("") || date.equals("") || numPics == 0) {
            Toast.makeText(getContext(), "Can't leave any required fields blank!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void configureDate (int value) {
        if (value < 10) {
            String valueStr = "0" + String.valueOf(value);
            date += valueStr;
        } else {
            date += String.valueOf(value);
        }
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
        while (code.length() < 4) { // length of the random string.
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
