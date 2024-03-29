package com.example.myapplication.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.Adapters.GuestAdapter;
import com.example.myapplication.Models.Event;
import com.example.myapplication.Models.UserNode;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.parceler.Parcels;
import org.w3c.dom.Text;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


public class EventDetailDescription extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;

    private TextView tvTitleDetailDescription;
    private TextView tvDescription;
    private TextView tvLocation;
    private TextView tvTimeDetail;
    private TextView tvDateDetail;
    private TextView tvPPP;
    private Button btnInvite;
    private Button btnBackDetail;

    final String TAG = "EventDetailDescription";

    public RecyclerView rvGuestList;
    public ArrayList<UserNode> guests;
    public GuestAdapter adapter;

    //Firebase things
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    FirebaseUser user;
    private UserNode currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail_description);

        tvTitleDetailDescription = (TextView) findViewById(R.id.tvTitleDetailDescription);
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        tvTimeDetail = (TextView) findViewById(R.id.tvTimeDetail);
        tvDateDetail = (TextView) findViewById(R.id.tvDateDetail);
        tvPPP = (TextView) findViewById(R.id.tvPPP);
        btnInvite = (Button) findViewById(R.id.btnInvite);
        btnBackDetail = (Button) findViewById(R.id.btnBackDetail);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) { //user is signed in

                } else {

                }
            }
        };

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                mAuth = FirebaseAuth.getInstance();
                user = mAuth.getCurrentUser();
                currentUser = dataSnapshot.child("UserNodes").child(user.getUid()).getValue(UserNode.class);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        btnBackDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        final Event event = (Event) Parcels.unwrap(getIntent().getParcelableExtra("event"));

        tvTitleDetailDescription.setText(event.title);
        tvDescription.setText(event.description);
        tvLocation.setText(event.location);
        tvTimeDetail.setText(event.time);
        int n = event.pics;
        tvPPP.setText(String.valueOf(n));
        String dateString = getDate(event.date);
        tvDateDetail.setText(dateString);

        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    Intent intent = new Intent(EventDetailDescription.this, ContactsListActivity.class);
                    intent.putExtra(Event.class.getSimpleName(), Parcels.wrap(event));
                    intent.putExtra("hostName", currentUser.name);
                    intent.putExtra("userCode", currentUser.userCode);
                    startActivity(intent);
                } else {
                    requestPermission();
                }

            }
        });

        // initialize recycler view
        rvGuestList = (RecyclerView) findViewById(R.id.rvGuestList);
        // initialize the arraylist (data source)
        guests = new ArrayList<>();
        // construct the adapter from this datasource
        adapter = new GuestAdapter(this, guests);
        // set the adapter on the recycler view
        rvGuestList.setAdapter(adapter);
        rvGuestList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        // populate contacts list
        populateGuestList(event);
    }

    private String getDate(String time) {
        String date = "";
        String year = time.substring(0,4);
        int month = Integer.parseInt(time.substring(4, 6)) - 1;
        String day = time.substring(6, 8);

        String m = "";
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (month >= 0 && month <= 11 ) {
            m = months[month];
        }
        date += m + " " + day + ", " + year;
        return date;
    }

    private boolean checkPermission() {
        int smsResult = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        int contactsResult = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        return smsResult == PackageManager.PERMISSION_GRANTED && contactsResult == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(EventDetailDescription.this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_CODE);
    }

    public void populateGuestList(Event event) {
        // Read from database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Event e = (Event) dataSnapshot.child("Events").child(event.accessCode).getValue(Event.class);
                for (String guestId : e.attending.keySet()) {
                    UserNode guestNode = (UserNode) dataSnapshot.child("UserNodes").child(guestId).getValue(UserNode.class);
                    guests.add(guestNode);
                    adapter.notifyItemInserted(guests.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value
                Log.e(TAG, "Failed to read value.", databaseError.toException());
            }
        });
    }
}
