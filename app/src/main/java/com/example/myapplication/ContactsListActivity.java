package com.example.myapplication;

import android.database.Cursor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.Models.Contact;

import java.util.ArrayList;

public class ContactsListActivity extends AppCompatActivity {

    private static final String TAG = "ContactsListActivity";

    private Button btnSendInvites;

    public RecyclerView rvContacts;
    public ArrayList<Contact> contacts;
    public ContactsAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list);
        btnSendInvites = findViewById(R.id.btnSendInvites);
        btnSendInvites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // list of selected phone numbers
                ArrayList<String> guestList = adapter.getInvitedList();
                for (String phoneNumber: guestList) {
                    SmsManager smsManager = SmsManager.getDefault();
                    String message = "Welcome to Grapefruit";
                    smsManager.sendTextMessage(phoneNumber, null, message, null , null);
                }
            }
        });
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
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,null,null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        assert phones != null;
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            Contact contactModel = new Contact();
            contactModel.setName(name);
            contactModel.setPhoneNumber(phoneNumber);
            contacts.add(contactModel);
            adapter.notifyItemInserted(contacts.size() - 1);
            Log.d(TAG,"Name: " + name + ", Number: " + phoneNumber);
        }
        phones.close();
    }


}
