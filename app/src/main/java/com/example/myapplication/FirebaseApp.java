package com.example.myapplication;

import android.app.Application;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseApp extends Application {

    FirebaseDatabase database;
    DatabaseReference ref;

    @Override
    public void onCreate() {
        super.onCreate();

        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
    }
}
