package com.example.myapplication.Models;

import android.util.Property;
import android.widget.EditText;

import com.example.myapplication.FirebaseApp;
import com.firebase.ui.auth.data.model.User;
import com.google.firebase.auth.FirebaseUser;
import com.example.myapplication.Models.Picture;

//import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Parcel
public class Event {
    public List<String> invited; //everyone that's invited
    public Map<String, Boolean> attending = new HashMap<>(); //list of everyone who has responsed "yes" to the invite
    public String title; //what the event is called
    public String host; //list of people in charge of event - maybe only a singular person?
    public String time; //time of date
    public String date; //event date
    public String description;
    public String location; //location of event
    public int pics; // number of pics per person
    public String accessCode;
    public Map<String, Boolean> allPictures = new HashMap<>(); //list of all pictures taken at event, needs to be updated when Picture class is made

    public Event() {
    }

    public Event(String host, String title, String time, String date, String description, String location, int pics,
                 List<String> invited, Map<String, Boolean> attending, Map<String, Boolean> allPictures, String accessCode) {
        this.title = title;
        this.host = host;
        this.time = time;
        this.date = date;
        this.description = description;
        this.location = location;
        this.pics = pics;
        this.attending = attending;
        this.allPictures = allPictures;
        this.accessCode = accessCode;
    }

}
