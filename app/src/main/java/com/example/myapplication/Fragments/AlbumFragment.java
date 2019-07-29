package com.example.myapplication.Fragments;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.Adapters.AlbumAdapter;
import com.example.myapplication.EndlessRecyclerViewScrollListener;
import com.example.myapplication.Models.Event;
import com.example.myapplication.Models.UserNode;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlbumFragment extends Fragment {

    protected RecyclerView rvEvents;
    public static final String TAG = "AlbumFragment";
    protected AlbumAdapter adapter;
    protected List<Event> mEvents;
    Map<String, Boolean> userEvents = new HashMap<>(1);
    private SwipeRefreshLayout swipeContainer;
    private EndlessRecyclerViewScrollListener scrollListener;

    // Get a reference to our posts
    final FirebaseDatabase database = FirebaseDatabase.getInstance();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_albums, container, false);
    }

    // This event is triggered soon after onCreteView()
    // Any view setup should occur here
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvEvents = view.findViewById(R.id.rvEvents);
        // disable double clicking on different posts
        rvEvents.setMotionEventSplittingEnabled(true);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);



        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        // create the data source
        mEvents = new ArrayList<>();
        // create the adapter
        adapter = new AlbumAdapter(getContext(), mEvents);
        // set the adapter on the recycler view
        rvEvents.setAdapter(adapter);
        // set the layout manager on the recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        rvEvents.setLayoutManager(linearLayoutManager);

        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                // loadNextDataFromApi();
            }
        };


        // Adds the scroll listener to RecyclerView
        rvEvents.addOnScrollListener(scrollListener);

         queryPosts();

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                adapter.clear();
                mEvents.clear();
                queryPosts();

            }
        });

    }


    /*
    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi() {
        maxId = mPosts.get(mPosts.size() - 1).getCreatedAt();
        queryPosts(maxId);
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
    }
    */

    protected void queryPosts() {

        DatabaseReference ref = database.getReference();
        DatabaseReference myEventRef = ref.child("Events");

        // Generate a reference to a new location and add some data using push()
        // DatabaseReference pushedPostRef = postsRef.push();

        ref.addValueEventListener(new ValueEventListener() {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserNode userInfo = dataSnapshot.child("UserNodes").child(user.getUid()).getValue(UserNode.class);
                userEvents = userInfo.eventsAttending; //gets map of events the user is attending
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        //ordering by "smallest" first RN
        //want it stored in the order: year, month, date //ADD ZEROS TO IT
        //get the list of events, go through the events and only publish the ones from the list
        myEventRef.orderByChild("date").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {

                //for(DataSnapshot eventSnapshot: dataSnapshot.getChildren()){
                    Event event = dataSnapshot.getValue(Event.class);

                    if (userEvents.containsKey(event.accessCode)) { //code from user list of events//
                        mEvents.add(event);
                        adapter.notifyDataSetChanged();
                    }
                    swipeContainer.setRefreshing(false);
                //}
                Log.d(TAG, "loaded events correctly");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }
}

