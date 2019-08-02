package com.example.myapplication.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

public class EventDetailDescription extends AppCompatActivity {

    private TextView tvTitleDetailDescription;
    private TextView tvDescription;
    private TextView tvLocation;
    private TextView tvTimeDetail;
    private TextView tvDateDetail;
    private TextView tvPPP;
    private Button btnInvite;

    final String TAG = "EventDetailDescription";



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


        final Event event = (Event) Parcels.unwrap(getIntent().getParcelableExtra("event"));

        tvTitleDetailDescription.setText(event.title);
        tvDescription.setText(event.description);
        tvLocation.setText(event.location);
        tvTimeDetail.setText(event.time);
        tvDateDetail.setText(event.date);
        int n = event.pics;
        tvPPP.setText(String.valueOf(n));

        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventDetailDescription.this, ContactsListActivity.class);
                intent.putExtra(Event.class.getSimpleName(), Parcels.wrap(event));
                intent.putExtra("hostName", currentUser.name);
                intent.putExtra("userCode", currentUser.userCode);
                startActivity(intent);
            }
        });
        
    }
}
