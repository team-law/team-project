package com.example.myapplication.Fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
<<<<<<< Updated upstream
=======
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
>>>>>>> Stashed changes
import android.util.Log;
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

import com.example.myapplication.ContactsListActivity;
import com.example.myapplication.HomeActivity;
import com.example.myapplication.LoginActivity;
import com.example.myapplication.Models.Event;
import com.example.myapplication.Models.Picture;
import com.example.myapplication.R;
import android.widget.ListView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class CreateEventFragment extends Fragment {


    private static final int PERMISSION_REQUEST_CODE = 1;

    private final String TAG = "CreateEventFragment";
    private TimePicker picker;
    private DatePicker datePicker;
    private EditText etEventDescription;
    private EditText etEventTitle;
    private EditText etLocation;
    private Button btnCreateEvent;
    private Button btnInviteFriends;
    private NumberPicker numberPicker;
    private Button btnSendInvite;

    public int hour, minute;
    public String am_pm;
    public int numPics = 2;
    public List<String> invited;
    Map<String, Event> events = new HashMap<>();
    //public FriendsAdapter adapter; //the adapter used for going through Facebook friends

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

        picker = view.findViewById(R.id.timePicker1);
        datePicker = view.findViewById(R.id.datePicker);
        etEventDescription = view.findViewById(R.id.etDescription);
        etEventTitle = view.findViewById(R.id.etEventTitle);
        etLocation = view.findViewById(R.id.etLocation);
        btnCreateEvent = view.findViewById(R.id.btnCreateEvent);
        btnInviteFriends = view.findViewById(R.id.btnInviteFriends);
        numberPicker = view.findViewById(R.id.numberPicker);

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

        btnInviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ContactsListActivity.class));
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
        picker.setIs24HourView(false);
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


        btnCreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // read the index key
                //Event event = new Event();
                String mGroupId = myRef.push().getKey();

                String title = etEventTitle.getText().toString();
                String description = etEventDescription.getText().toString();
                String date = String.valueOf(datePicker.getMonth()) + String.valueOf(datePicker.getDayOfMonth())
                        + String.valueOf(datePicker.getYear());
                String time = String.valueOf(hour) + minute + am_pm;
                String location = etLocation.getText().toString();
                invited = new ArrayList<>(); //should be retrieved from the search view
                Map<String, Boolean> attending = new HashMap<>(1);
                attending.put(user.getUid(), true);
                Map<String, Boolean> pics = new HashMap<>(0);
                String accessCode = getCode();

                DatabaseReference eventsRef = myRef.child("Events");

                Event event = new Event(user.getUid(), title, time, date, description, location, numPics, invited, attending, pics, accessCode);
                eventsRef.child(accessCode).setValue(event); //creates the event in firebase

                //add event to host list of events
                DatabaseReference userEventRef = myRef.child("UserNodes").child(user.getUid()).child("eventsAttending");
                userEventRef.child(accessCode).setValue(true); //adds the event to the user's list of events, marks true as them being the host

                Toast.makeText(getActivity(), "Event created successfully!", Toast.LENGTH_SHORT).show();

                //reset all the fields in the create fragment
                etEventTitle.setText("");
                etEventDescription.setText("");
                etLocation.setText("");
                numberPicker.setValue(2);

            }
        });

        // temporary button to send a text message
        btnSendInvite = view.findViewById(R.id.btnSendInvite);
        btnSendInvite.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (checkPermission()) {
                    Log.e("permission", "Permission already granted.");
                } else {
                    requestPermission();
                }
                // send sms invite to users
                sendInvite();

            }
        });
    }

    private void sendInvite() {
        if (checkPermission()) {
            SmsManager smsManager = SmsManager.getDefault();
            String message = "Welcome to Grapefruit";
            smsManager.sendTextMessage("17874074524", null, message, null , null);
        }
    }

<<<<<<< Updated upstream

    public String getCode() {
        //generate randomized access code and check to see if it already exists
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder code = new StringBuilder();
        Random rand = new Random();
        while (code.length() < 7) { // length of the random string.
            int index = rand.nextInt(characters.length());
            code.append(characters.charAt(index));
        }
        String finalCode = code.toString();
        return finalCode;
    }

    NumberPicker.OnValueChangeListener onValueChangeListener = new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                    numPics = numberPicker.getValue();

=======
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_CODE);
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
>>>>>>> Stashed changes

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

    NumberPicker.OnValueChangeListener onValueChangeListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker numberPicker, int i, int i1) {
            numPics = numberPicker.getValue();
        }
    };

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
