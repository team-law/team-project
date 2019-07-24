package com.example.myapplication;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class ContactsListActivity extends AppCompatActivity {

    public RecyclerView rvContacts;
    public ArrayList<Contacts> contacts;
    public ContactsAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list);
        // initialize recycler view
        rvContacts = findViewById(R.id.rvContacts);
        // initialize the arraylist (data source)
        contacts = new ArrayList<>();
        // construct the adapter from this datasource
        adapter = new ContactsAdapter(contacts);
        // set the adapter on the recycler view
        rvContacts.setAdapter(adapter);
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        // populate contacts list
        populateContactsList();
    }

    public void populateContactsList() {

    }
}
