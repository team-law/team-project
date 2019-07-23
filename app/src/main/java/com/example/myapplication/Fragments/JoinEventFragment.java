package com.example.myapplication.Fragments;

import android.os.Bundle;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

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
    //might need to be using the FirebaseApp ones?

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
                        //Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                        for (DataSnapshot snapshot: dataSnapshot.child("Events").getChildren()) {
                            String accessCode = (String) snapshot.child("accessCode").getValue();

                            if (accessCode != null && accessCode.equals(code)) {
                                //if event does exist, add user to guestlist, reset edittext, submit success text
                                Event event = snapshot.getValue(Event.class);
                                //ArrayList<String> attending = (ArrayList<String>) snapshot.child("attending").getValue();
                                ArrayList<String> attending = (ArrayList<String>) event.attending;
                                attending.add("new guest"); //TODO fix this to add a user's name
                                //event.attending = attending;
                                //myRef.child("Events").child(snapshot.getKey()).child("attending").setValue(attending); BAD
                                //dataSnapshot.child("Events").getValue(Event.class).setAttending(attending);
                                //myRef.child(snapshot.getKey()).child("attending").setValue(attending);
                                //snapshot.child("attending").setValue(attending);
                                String mGroupId = myRef.getKey();
                                myRef.child("Events").child(mGroupId).child("attending").setValue(attending);
                                etEventCode.setText("");
                                Toast.makeText(getActivity(), "Successfully joined event!", Toast.LENGTH_SHORT).show();
                                joined = true;
                                break;
                            }


//                            if(messageSnapshot.accessCode.equals(code)){
//                                retrievedUser = messageSnapshot.getValue(Event.class);
//                                doSomethingWithTheUser(retrievedUser);
//                            }
                        }

                        if (!joined) {
                            Toast.makeText(getActivity(), "No event exists with that code, please try again", Toast.LENGTH_LONG).show();
                        }
                        //String value = dataSnapshot.getValue(String.class);
                        //Log.d(TAG, "Value is: " + map);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }

                });
            }
        });







    }
}
