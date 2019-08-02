package com.example.myapplication.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.myapplication.Models.Event;
import com.example.myapplication.R;

import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        int n = event.pics;
        tvPPP.setText(String.valueOf(n));
        String dateString = getDate(event.date);
        tvDateDetail.setText(dateString);
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
}
