package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private static final String EMAIL = "email";

    private LoginButton loginButton;
    private CallbackManager callbackManager = CallbackManager.Factory.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
<<<<<<< Updated upstream
=======

        // initialize Firebase authorization and Callback manager
        mAuth = FirebaseAuth.getInstance();
>>>>>>> Stashed changes
        callbackManager = CallbackManager.Factory.create();

        // sync login button
        loginButton = (LoginButton) findViewById(R.id.login_button);
<<<<<<< Updated upstream
        loginButton.setReadPermissions(Arrays.asList(EMAIL));
=======
        // request permission to access email and public profile
        loginButton.setReadPermissions("email", "public_profile");
>>>>>>> Stashed changes

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
<<<<<<< Updated upstream
                // App code
=======
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                // check login status
                handleFacebookAccessToken(loginResult.getAccessToken());
>>>>>>> Stashed changes
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
<<<<<<< Updated upstream
=======

>>>>>>> Stashed changes
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // pass the login results via callbackManager
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
