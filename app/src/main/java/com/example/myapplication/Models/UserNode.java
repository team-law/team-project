package com.example.myapplication.Models;

import com.example.myapplication.Models.Picture;

import com.google.firebase.auth.FirebaseUser;

import org.parceler.Parcel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Parcel
public class UserNode {

    public String userId;
    //public String invitedBy;
    //public String event;
    //public boolean isHost;
    public Map<String, Boolean> eventsAttending = new HashMap<>(1);
    public Map<String, Picture> picturesTaken = new HashMap<>(1); //maps the eventID with a picture taken

    public UserNode() {
        // Default constructor required for calls to DataSnapshot.getValue(UserNode.class)
    }

    public UserNode(String userId, Map<String, Picture> picturesTaken, Map<String, Boolean> eventsAttending) {
        this.userId = userId;
        //this.invitedBy = invitedBy;
        //this.event = event;
        //this.isHost = isHost;
        this.eventsAttending = eventsAttending;
        this.picturesTaken = picturesTaken;
    }

}
