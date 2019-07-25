package com.example.myapplication.Models;

public class Contact {

    private String name;
    private String phoneNumber;
    private boolean selected;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public boolean isSelected() { return selected; }

    public void toggleSelected() { this.selected = !this.selected; }
}
