package com.example.myapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.myapplication.Fragments.AlbumFragment;
import com.example.myapplication.Fragments.CreateEventFragment;
import com.example.myapplication.Fragments.EventAlbumViewFragment;
import com.example.myapplication.Fragments.ProfileFragment;
import com.example.myapplication.Models.Event;
import com.firebase.ui.auth.data.model.User;

import org.parceler.Parcels;

import static com.example.myapplication.R.id.action_invites;
import static com.example.myapplication.R.id.action_new_event;
import static com.example.myapplication.R.id.action_profile;

public class EventDetail extends AppCompatActivity {

    private FloatingActionButton btnNewPhoto;
    //Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        btnNewPhoto = (FloatingActionButton) findViewById(R.id.btnNewPhoto);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Event event = (Event) Parcels.unwrap(getIntent().getParcelableExtra("event"));


        TextView textView = (TextView) toolbar.findViewById(R.id.toolbarTextView);
        textView.setText(event.title);

        btnNewPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventDetail.this, CameraActivity.class);
                startActivity(intent);
            }
        });


        final Fragment eventAlbum = new EventAlbumViewFragment();

        FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction transaction = fm.beginTransaction();


        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rgToggle);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected

                switch(checkedId) {
                    case R.id.rbMap:
                        Log.d("Event Detail", "Map");

                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.flEventContainer,new EventAlbumViewFragment());
                        fragmentTransaction.commit();
                        break;
                    case R.id.rbAlbum:
                        Log.d("Event Detail", "Album");
                       // transaction.replace(R.id.flEventContainer, eventAlbum);
                        //transaction.commit();
                        break;
                }
            }
        });

    }



}
