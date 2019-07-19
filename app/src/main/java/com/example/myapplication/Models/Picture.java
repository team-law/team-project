package com.example.myapplication.Models;

import android.media.Image;
import android.net.Uri;

import java.net.URI;

public class Picture {

    public Uri image;
    public UserNode userNode;
    public String createdAt;

    public Picture() {
        // Default constructor required for calls to DataSnapshot.getValue(Picture.class)
    }

    public Picture(Uri image, UserNode userNode, String createdAt) {
        this.image = image;
        this.userNode = userNode;
        this.createdAt = createdAt;
    }

}
