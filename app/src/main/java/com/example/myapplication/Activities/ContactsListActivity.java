package com.example.myapplication.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcel;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.myapplication.Adapters.ContactsAdapter;
import com.example.myapplication.Fragments.CreateEventFragment;
import com.example.myapplication.Models.Contact;
import com.example.myapplication.Models.Event;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.parceler.Parcels;

import java.util.ArrayList;

public class ContactsListActivity extends AppCompatActivity {

    private static final String TAG = "ContactsListActivity";

    private Button btnSendInvites;

    public RecyclerView rvContacts;
    public ArrayList<Contact> contacts;
    public ContactsAdapter adapter;

    //Firebase things
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    FirebaseUser user;

    String name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list);
        btnSendInvites = findViewById(R.id.btnSendInvites);
        final Event event = (Event) Parcels.unwrap(getIntent().getParcelableExtra(Event.class.getSimpleName()));
        name = getIntent().getStringExtra("hostName");

//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                user = firebaseAuth.getCurrentUser();
//                name = user.getDisplayName();
//                if (user != null) { //user is signed in
//                } else {
//                    name = null;
//                }
//            }
//        };

        btnSendInvites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String message = name + " just invited you to " + event.title
                        + "! Open or download Grapefruit to join the event! Access code: " + event.accessCode + ".";
                // list of selected phone numbers
                ArrayList<String> guestList = adapter.getInvitedList();
                for (String phoneNumber: guestList) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNumber, null, message, null , null);
                }
                Toast.makeText(ContactsListActivity.this, "Successfully created and invited people to event!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(ContactsListActivity.this, HomeActivity.class));
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
