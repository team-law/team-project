package com.example.myapplication.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.AlbumAdapter;
import com.example.myapplication.EndlessRecyclerViewScrollListener;
import com.example.myapplication.EventDetail;
import com.example.myapplication.Models.Event;
import com.example.myapplication.Models.Picture;
import com.example.myapplication.PhotoAdapter;
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

        mPhotoRefs = new ArrayList<>();
        if (getArguments() != null) {

            event = getArguments().getParcelable("event");
            attending = (HashMap) getArguments().getSerializable("attending");
            allPictures = (HashMap) getArguments().getSerializable("allPictures");
            Log.d("AlbumViewFrag", event.accessCode);

        }

        // all photo refs to mPhotoRef list
        for (Map.Entry<String, Boolean> entry : allPictures.entrySet()) {
            String s = entry.getKey();
            mPhotoRefs.add(s);
        }

        database = FirebaseDatabase.getInstance();

        mPictures = new ArrayList<>();
        // create the adapter
        adapter = new PhotoAdapter(getContext(), mPictures);
        // set the adapter on the recycler view
        rvEventPictures.setAdapter(adapter);
        // set the layout manager on the recycler view
        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        rvEventPictures.setLayoutManager(mLayoutManager);

        queryPics();


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

    }
}
