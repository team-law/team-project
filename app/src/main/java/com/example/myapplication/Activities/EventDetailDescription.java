package com.example.myapplication.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.Models.Event;
import com.example.myapplication.R;

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

        final Event event = (Event) Parcels.unwrap(getIntent().getParcelableExtra("event"));

        tvTitleDetailDescription.setText(event.title);
        tvDescription.setText(event.description);
        tvLocation.setText(event.location);
        tvTimeDetail.setText(event.time);
        tvDateDetail.setText(event.date);
        int n = event.pics;
        tvPPP.setText(String.valueOf(n));

        
    }
}
