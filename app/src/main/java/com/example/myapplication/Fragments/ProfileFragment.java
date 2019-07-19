package com.example.myapplication.Fragments;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.FirebaseApp;
import com.example.myapplication.HomeActivity;
import com.example.myapplication.LoginActivity;
import com.example.myapplication.R;
import com.facebook.Profile;
import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class ProfileFragment extends Fragment {

    private Button btnSignout;
    private ImageView ivProfilePicture;
    private TextView tvUsername;
    private TextView tvEmail;
    private TextView tvAbout;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Profile profile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    // This event is triggered soon after onCreteView()
    // Any view setup should occur here
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize Firebase Auth and User
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        Profile profile = Profile.getCurrentProfile();

        // Initialize objects in profile fragment
        ivProfilePicture = (ImageView) view.findViewById(R.id.ivProfilePicture);
        tvUsername = (TextView) view.findViewById(R.id.tvUsername);
        tvEmail = (TextView) view.findViewById(R.id.tvEmail);

        // Fetch Facebook information and to insert in objects
        Glide.with(this).load(profile.getProfilePictureUri(400, 400)).into(ivProfilePicture);
//        tvUsername.setText(profile.getName());
//        Glide.with(this).load(user.getPhotoUrl()).into(ivProfilePicture);
        tvUsername.setText(user.getDisplayName());
        tvEmail.setText(user.getEmail());

        btnSignout = (Button) view.findViewById(R.id.btnLogout);
        btnSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth .signOut();
                LoginManager.getInstance().logOut();
                updateUI();
           }
        });

    }

    private void updateUI() {
        Toast.makeText(getContext(), "You are logged out!", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        // check if user was already logged-in (user persistence)
        if (mAuth.getCurrentUser() == null) {
            updateUI();
        }
    }

}
