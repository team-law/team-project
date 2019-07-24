package com.example.myapplication.Models;

import android.graphics.Picture;

import com.google.firebase.auth.FirebaseUser;

import org.parceler.Parcel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Parcel
public class UserNode {

    //public String user;
    //public String invitedBy;
    public String event;
    public boolean isHost;
    public Map<String, Boolean> events = new HashMap<>(1);
    public Map<String, Picture> picturesTaken = new HashMap<>(1); //maps the eventID with a picture taken

    public UserNode() {
        // Default constructor required for calls to DataSnapshot.getValue(UserNode.class)
    }

    public UserNode(Map<String, Picture> picturesTaken, Map<String, Boolean> events) {
        //this.user = user;
        //this.invitedBy = invitedBy;
        //this.event = event;
        //this.isHost = isHost;
        this.events = events;
        this.picturesTaken = picturesTaken;
    }

}
