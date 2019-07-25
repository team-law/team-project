package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.myapplication.Models.Picture;
import com.example.myapplication.Models.UserNode;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.LoginStatusCallback;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    // Permissions
    private static final String EMAIL = "email";
    private static final String PUBLIC_PROFILE = "public_profile";
    private static final String USER_FRIENDS = "user_friends";

    // UI Objects
    private Button btnFBLogin;
    private ProgressBar pb;

    // Database declaration
    private FirebaseAuth mAuth;
    private CallbackManager callbackManager;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // initialize Firebase authorization and Callback manager
        mAuth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();

        // sync login button and progress bar
        btnFBLogin = (Button) findViewById(R.id.btnFBLogin);
        pb = (ProgressBar) findViewById(R.id.pbLoading);

        btnFBLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(ProgressBar.VISIBLE);
                // request permission to access email and public profile
                LoginManager.getInstance()
                            .logInWithReadPermissions(LoginActivity.this, Arrays.asList(EMAIL, PUBLIC_PROFILE, USER_FRIENDS));
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // prevents user from clicking on button again while logging in
                        btnFBLogin.setEnabled(false);
                        // App code
                        Log.d(TAG, "facebook:onSuccess:" + loginResult);
                        // check login status
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "facebook:onCancel");
                        // ...
                        pb.setVisibility(ProgressBar.INVISIBLE);
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.d(TAG, "facebook:onError", exception);
                        // ...
                        pb.setVisibility(ProgressBar.INVISIBLE);
                    }
                });
            }
        });
    }

    private void updateUI() {
        Toast.makeText(this, "You are logged in!", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        pb.setVisibility(ProgressBar.INVISIBLE);
        finish();
        //check to see if this user already exists - if it doesn't then get the UserID from FB and create new UserNode object
        checkUser();
    }

    private void checkUser() {
        FirebaseAuth.AuthStateListener mAuthListener;
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        final FirebaseUser user;
        user = mAuth.getCurrentUser();

        myRef.addValueEventListener(new ValueEventListener() {
            DatabaseReference usersRef = myRef.child("UserNodes");
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean userExists = false;
                //for every user in Users
                for (DataSnapshot snapshot : dataSnapshot.child("UserNodes").getChildren()) {
                    if ((snapshot.getKey()).equals(user.getUid())) {
                        userExists = true;
                        break;

                    }
                }

                if (!userExists) {
                    //create a new user class for that person
                    //String mGroupId = usersRef.push().getKey();
                    Map<String, String> picturesTaken = new HashMap<>(1);
                    Map<String, Boolean> eventsAttending = new HashMap<>(1);
                    String name = user.getDisplayName(); //might error if the user doesn't have a display name
                    UserNode userProfile = new UserNode(user.getUid(), name, picturesTaken, eventsAttending);
                    usersRef.child(user.getUid()).setValue(userProfile); //creates the userNode in firebase
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // check if user was already logged-in (user persistence)
        if (mAuth.getCurrentUser() != null) {
            updateUI();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // pass the login results via callbackManager
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            btnFBLogin.setEnabled(true);
                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            btnFBLogin.setEnabled(true);
                            updateUI();
                        }

                        // ...
                    }
                });
    }
}
