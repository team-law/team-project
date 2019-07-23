package com.example.myapplication.Fragments;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.Models.Event;
import com.example.myapplication.R;
import com.firebase.ui.auth.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class JoinEventFragment extends Fragment {
    private final String TAG = "JoinEventFragment";

    Button btnJoinEvent;
    EditText etEventCode;

    //Firebase things
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    FirebaseUser user;

    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_join_event, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnJoinEvent = view.findViewById(R.id.btnJoin);
        etEventCode = view.findViewById(R.id.etEventCode);

        btnJoinEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get entered event code
                final String code = etEventCode.getText().toString();

                //check through event database to see if there's an event that has that code
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
                        boolean joined = false;
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.

                        //for every event in Events
                        for (DataSnapshot snapshot: dataSnapshot.child("Events").getChildren()) {

                            if ((snapshot.getKey()).equals(code)) {
                                DatabaseReference eventRef = myRef.child("Events").child(code);
                                DatabaseReference dbRef = eventRef.child("attending");

                                dbRef.child(user.getUid()).setValue(true); //add user to guest list by their user ID

                                etEventCode.setText("");
                                Toast.makeText(getActivity(), "Successfully joined event!", Toast.LENGTH_SHORT).show();
                                joined = true;
                                break;

                            }
                        }

                        if (!joined) {
                            etEventCode.setText("");
                            Toast.makeText(getActivity(), "No event exists with that code, please try again", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        //Log.w(TAG, "Failed to read value.", error.toException());
                    }

                });
            }
        });

    }
}
