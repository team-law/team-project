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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.LoginStatusCallback;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
//    private static final String EMAIL = "email";

//    private LoginButton loginButton;
    private Button btnFBLogin;
    private ProgressBar pb;

    private FirebaseAuth mAuth;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance();


        // initialize Firebase authorization and Callback manager
        mAuth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();

        // sync login button and progress bar
        btnFBLogin = (Button) findViewById(R.id.btnFBLogin);
        pb = (ProgressBar) findViewById(R.id.pbLoading);
//        loginButton = (LoginButton) findViewById(R.id.login_button);
//        loginButton.setReadPermissions("email", "public_profile");
//        loginButton.setReadPermissions(Arrays.asList("name", "email", "picture"));
        // request permission to access email and public profile

        btnFBLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(ProgressBar.VISIBLE);
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));
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

        LoginManager.getInstance().retrieveLoginStatus(this, new LoginStatusCallback() {
            @Override
            public void onCompleted(AccessToken accessToken) {
                // User was previously logged in, can log them in directly here.
                // If this callback is called, a popup notification appears that says
                // "Logged in as <User Name>"
            }
            @Override
            public void onFailure() {
                // No access token could be retrieved for the user
            }
            @Override
            public void onError(Exception exception) {
                // An error occurred
            }
        });

    }

    private void updateUI() {
        Toast.makeText(this, "You are logged in!", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        pb.setVisibility(ProgressBar.INVISIBLE);
        finish();
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
//                            FirebaseUser user = mAuth.getCurrentUser();
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
