package com.example.myapplication.Activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.myapplication.Fragments.EventAlbumViewFragment;
import com.example.myapplication.Fragments.EventMapViewFragment;
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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class EventDetail extends AppCompatActivity {

    private FloatingActionButton btnNewPhoto;
    // Event event;
    private HashMap<String, Boolean> attending;
    private HashMap<String, Boolean> allPictures;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    boolean clicked;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        btnNewPhoto = (FloatingActionButton) findViewById(R.id.btnNewPhoto);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        final Event event = (Event) Parcels.unwrap(getIntent().getParcelableExtra("event"));

        attending = (HashMap) getIntent().getSerializableExtra("attending");
        allPictures = (HashMap) getIntent().getSerializableExtra("allPictures");

        TextView textView = (TextView) toolbar.findViewById(R.id.toolbarTextView);
        textView.setText(event.title);

        btnNewPhoto.setOnClickListener(new View.OnClickListener() {
            int count = 0;
            int numPics = event.pics;
            @Override
            public void onClick(View v) {
                clicked = true;
                final DatabaseReference ref = database.getReference();
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        FirebaseUser user = mAuth.getCurrentUser();
                        UserNode userInfo = dataSnapshot.child("UserNodes").child(user.getUid()).getValue(UserNode.class);
                        Map<String, String> userPics = new HashMap<>(1);
                        userPics = userInfo.picturesTaken; //gets map of pictures the user has taken

                        if(clicked) {
                            //look through each picture and if the value is the current event ID then increment the count
                            for (String value : userPics.values()) {
                                if (value.equals(event.accessCode)) {
                                    count++;
                                }
                            }

                            if (count < numPics) { //if the user still has pictures they can take for an event
                                Intent intent = new Intent(EventDetail.this, CameraActivity.class);
                                intent.putExtra("event", Parcels.wrap(event));
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), "You have already reached the limit for the number of pictures you can take for this event!",
                                        Toast.LENGTH_LONG).show();
                            }
                            clicked = false;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());
                    }
                });


            }
        });

        // initialize fragments
        final Fragment mapFragment = new EventMapViewFragment();
        final Fragment albumFragment = new EventAlbumViewFragment();

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rgToggle);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                switch(checkedId) {
                    case R.id.rbMap:

                        FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction1.replace(R.id.flEventContainer, mapFragment);
                        fragmentTransaction1.commit();

                        break;
                    case R.id.rbAlbum:

                        Bundle bundle = new Bundle();

                        Event e = event;

                        bundle.putParcelable("event", e);
                        bundle.putSerializable("attending", (Serializable) attending);
                        bundle.putSerializable("allPictures", (Serializable) allPictures);
                        albumFragment.setArguments(bundle);

                        FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction2.replace(R.id.flEventContainer, albumFragment);
                        fragmentTransaction2.commit();

                        break;
                }
            }
        });
    }



}
