package com.example.myapplication.Models;

import android.annotation.SuppressLint;
import android.os.Parcelable;
import android.util.Property;
import android.widget.EditText;

import com.example.myapplication.FirebaseApp;
import com.firebase.ui.auth.data.model.User;
import com.google.firebase.auth.FirebaseUser;
import com.example.myapplication.Models.Picture;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Parcel
public class Event implements Parcelable /* implements Parcelable*/ {
    public List<String> invited; //everyone that's invited
    public Map<String, String> attending = new HashMap<>(); //list of everyone who has responsed "yes" to the invite
    public String title; //what the event is called
    public String host; //list of people in charge of event - maybe only a singular person?
    public String time; //time of date
    public String date; //event date
    public String description;
    public String location; //location of event
    public int pics; // number of pics per person
    public String hostName;
    public String accessCode;
    public boolean passed;
    public Map<String, Boolean> allPictures = new HashMap<>(); //list of all pictures taken at event, needs to be updated when Picture class is made

    public Event() {
    }

    public Event(String host, String hostName, String title, String time, String date, String description, String location, int pics,
                 List<String> invited, Map<String, String> attending, Map<String, Boolean> allPictures, String accessCode, boolean passed) {
        this.host = host;
        this.hostName = hostName;
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
        this.passed = passed;
    }

    public Event(android.os.Parcel in) {
        invited = in.createStringArrayList();
        title = in.readString();
        host = in.readString();
        time = in.readString();
        date = in.readString();
        description = in.readString();
        location = in.readString();
        pics = in.readInt();
        accessCode = in.readString();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(android.os.Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeStringList(invited);
        dest.writeString(title);
        dest.writeString(host);
        dest.writeString(time);
        dest.writeString(date);
        dest.writeString(description);
        dest.writeString(location);
        dest.writeInt(pics);
        dest.writeString(accessCode);
    }

    @Override
    public boolean equals(Object o){
        if(this == o)
            return true;
        if(!( o instanceof Event))
            return false;

        Event f = (Event) o;
        if(this.accessCode.equals(f.accessCode))
            return true;
        return false;
    }

    @Override
    public int hashCode() {
        return accessCode.hashCode();
    }

/*
    protected Event(android.os.Parcel in) {
        invited = in.createStringArrayList();
        title = in.readString();
        host = in.readString();
        time = in.readString();
        date = in.readString();
        description = in.readString();
        location = in.readString();
        pics = in.readInt();
        accessCode = in.readString();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(android.os.Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeStringList(invited);
        dest.writeMap(attending);
        dest.writeString(title);
        dest.writeString(host);
        dest.writeString(time);
        dest.writeString(description);
        dest.writeString(location);
        dest.writeInt(pics);
        dest.writeString(accessCode);
        dest.writeMap(allPictures);
    }
*/
}