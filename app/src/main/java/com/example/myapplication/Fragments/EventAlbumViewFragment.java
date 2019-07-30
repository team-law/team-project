package com.example.myapplication.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.EndlessRecyclerViewScrollListener;
import com.example.myapplication.Models.Event;
import com.example.myapplication.Models.Picture;
import com.example.myapplication.Adapters.PhotoAdapter;
import com.example.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventAlbumViewFragment extends Fragment {

    private RecyclerView rvEventPictures;
    private Event event;

    public static final String TAG = "EventAlbumViewFragment";
    protected PhotoAdapter adapter;
    protected List<Picture> mPictures;

    private HashMap<String, Boolean> attending;
    private HashMap<String, Boolean> allPictures;

    private ArrayList<String> mPhotoRefs;

    // Get a reference to our posts
    private FirebaseDatabase database;

    private SwipeRefreshLayout scEventDetail;
    private EndlessRecyclerViewScrollListener scrollListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_detail_album, container, false);
    }

    // This event is triggered soon after onCreteView()
    // Any view setup should occur here
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvEventPictures = view.findViewById(R.id.rvEventPictures);

        scEventDetail = (SwipeRefreshLayout) view.findViewById(R.id.scEventDetail);



        // Configure the refreshing colors
        scEventDetail.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mPhotoRefs = new ArrayList<>();
        if (getArguments() != null) {

            event = getArguments().getParcelable("event");


//            attending = (HashMap) getArguments().getSerializable("attending");
//            allPictures = (HashMap) getArguments().getSerializable("allPictures");


        }

        database = FirebaseDatabase.getInstance();

        networkCall();



        mPictures = new ArrayList<>();
        // create the adapter
        adapter = new PhotoAdapter(getContext(), mPictures);
        // set the adapter on the recycler view
        rvEventPictures.setAdapter(adapter);
        // set the layout manager on the recycler view
        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        rvEventPictures.setLayoutManager(mLayoutManager);

        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                // loadNextDataFromApi();
            }
        };
        // Adds the scroll listener to RecyclerView
        rvEventPictures.addOnScrollListener(scrollListener);


        scEventDetail.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                adapter.clear();
                mPhotoRefs.clear();
                networkCall();

            }
        });

    }

    protected void networkCall(){
        DatabaseReference ref = database.getReference("Events/" + event.accessCode);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Event ev = dataSnapshot.getValue(Event.class);
                attending = (HashMap) ev.attending;
                allPictures = (HashMap) ev.allPictures;

                // all photo refs to mPhotoRef list
                for (Map.Entry<String, Boolean> entry : allPictures.entrySet()) {
                    String s = entry.getKey();
                    mPhotoRefs.add(s);
                }

                queryPics();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }

        });

    }

    protected void queryPics() {


        // DatabaseReference ref = database.getReference("Picture");

        // Generate a reference to a new location and add some data using push()
        // DatabaseReference pushedPostRef = postsRef.push();

        for (int i = 0; i < mPhotoRefs.size(); i++) {
            String photoRef = mPhotoRefs.get(i);
            DatabaseReference ref = database.getReference("Picture/" + photoRef);

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Picture picture = dataSnapshot.getValue(Picture.class);
                    mPictures.add(picture);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });

        }
        scEventDetail.setRefreshing(false);
    }
}
