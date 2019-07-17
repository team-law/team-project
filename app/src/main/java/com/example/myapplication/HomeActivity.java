package com.example.myapplication;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

<<<<<<< Updated upstream
=======
import com.example.myapplication.Fragments.AlbumFragment;

import static com.example.myapplication.R.id.action_camera;
import static com.example.myapplication.R.id.action_invites;
import static com.example.myapplication.R.id.action_new_event;
import static com.example.myapplication.R.id.action_profile;

>>>>>>> Stashed changes
public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

       /* bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        });
        */

        final FragmentManager fragmentManager = getSupportFragmentManager();

        // define your fragments here
        final Fragment posts = new PostsFragment();
        final Fragment addPhoto = new ComposeFragment();
        final Fragment profile = new ProfileFragment();


        // handle navigation selection
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment fragment;
                        switch (item.getItemId()) {
                            default:
                                fragment = posts;
                                break;
<<<<<<< Updated upstream
                            case R.id.action_search:
                                fragment = addPhoto;
                                break;
                            case R.id.action_add_photo:
                                fragment = addPhoto;
                                break;
                            case R.id.action_likes:
                                fragment = addPhoto;
                                break;
                            case R.id.action_profile:
                                fragment = profile;
=======
                            case action_new_event:
                                fragment = albums;
                                break;
                            case action_camera:
                                fragment = albums;
                                break;
                            case action_invites:
                                fragment = albums;
                                break;
                            case action_profile:
                                fragment = albums;
>>>>>>> Stashed changes
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
