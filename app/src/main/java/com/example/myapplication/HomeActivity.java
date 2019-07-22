package com.example.myapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.myapplication.Fragments.AlbumFragment;
import com.example.myapplication.Fragments.CreateEventFragment;
import com.example.myapplication.Fragments.ProfileFragment;

import static com.example.myapplication.R.id.action_cam;
import static com.example.myapplication.R.id.action_invites;
import static com.example.myapplication.R.id.action_new_event;
import static com.example.myapplication.R.id.action_profile;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        final FragmentManager fragmentManager = getSupportFragmentManager();

        // define your fragments here
        final Fragment albums = new AlbumFragment();
        final Fragment createEvent = new CreateEventFragment();

//        final Fragment addPhoto = new ComposeFragment();
        final Fragment profile = new ProfileFragment();

        // handle navigation selection
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment fragment;
                        switch (item.getItemId()) {
                            default:
                                fragment = albums;
                                break;
                            case action_new_event:
                                fragment = createEvent;
                                break;
                            case action_cam:
                                fragment = albums;
                                break;
                            case action_invites:
                                fragment = albums;
                                break;
                            case action_profile:
                                fragment = profile;
                                break;
                        }
                        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                        return true;
                    }
                });
        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.action_home);
    }

}
