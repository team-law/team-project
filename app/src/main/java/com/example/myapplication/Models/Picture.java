package com.example.myapplication.Models;

import android.media.Image;
import android.net.Uri;

import org.parceler.Parcel;

import java.net.URI;

@Parcel
public class Picture {

    public String imageRef;
    public String user;
    public String createdAt;

    // Default constructor required for calls to DataSnapshot.getValue(Picture.class)
    public Picture() { }

    public Picture(String imageRef, String user, String createdAt) {
        this.imageRef = imageRef;
        this.user = user;
        this.createdAt = createdAt;
    }

}
