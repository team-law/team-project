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
    public String name;
    public String userCode;
    //public String invitedBy;
    //public String event;
    //public boolean isHost;
    public Map<String, Boolean> eventsAttending = new HashMap<>(1);
    public Map<String, String> picturesTaken = new HashMap<>(1); //maps the pictureID with an eventID

    public UserNode() {
        // Default constructor required for calls to DataSnapshot.getValue(UserNode.class)
    }

    public UserNode(String userId, String name, String userCode, Map<String, String> picturesTaken, Map<String, Boolean> eventsAttending) {
        this.userId = userId;
        this.name = name;
        this.userCode = userCode;
        //this.invitedBy = invitedBy;
        //this.event = event;
        //this.isHost = isHost;
        this.eventsAttending = eventsAttending;
        this.picturesTaken = picturesTaken;
    }

}
