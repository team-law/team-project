package com.example.myapplication.Models;

import android.graphics.Picture;
import android.util.Property;

import com.example.myapplication.FirebaseApp;

import java.util.List;

public class Event extends FirebaseApp {
    private List<User> invited; //everyone that's invited
    private List<User> attending; //list of everyone who has responsed "yes" to the invite
    private String title; //what the event is called
    private List<User> hosts; //list of people in charge of event - maybe only a singular person?
    private String time; //time of date
    private String date; //event date
    private String description;
    private String location; //location of event
    private int pics; // number of pics per person
    private List<Picture> allPictures; //list of all pictures taken at event, needs to be updated when Picture class is made

    public Event() {
    }

}
