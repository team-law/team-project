package com.example.myapplication.Models;

import android.graphics.Picture;

import com.google.firebase.auth.FirebaseUser;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class UserNode {

    public FirebaseUser user;
    public FirebaseUser invitedBy;
    public Event event;
    public boolean isHost;
    // public List<Picture> picturesTaken;

    public UserNode() {
        // Default constructor required for calls to DataSnapshot.getValue(UserNode.class)
    }

    public UserNode(FirebaseUser user, FirebaseUser invitedBy, Event event, boolean isHost, List<Picture> picturesTaken) {
        this.user = user;
        this.invitedBy = invitedBy;
        this.event = event;
        this.isHost = isHost;
        // this.picturesTaken = picturesTaken;
    }

}
