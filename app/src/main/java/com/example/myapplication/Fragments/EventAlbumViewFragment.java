package com.example.myapplication.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.EventDetail;
import com.example.myapplication.Models.Event;
import com.example.myapplication.R;

public class EventAlbumViewFragment extends Fragment {

    private RecyclerView rvEventPictures;
    private Event event;

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
        rvEventPictures = view.findViewById(R.id.rvEventPictures);

        /*
        if (getArguments() != null) {
         
            event = getArguments().getParcelable("event");
            Log.d("AlbumViewFrag", event.accessCode);

        }
        */

//        Log.d("AlbumViewFrag", EventDetail.event);
//
//        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(),2);
//        rvEventPictures.setLayoutManager(mLayoutManager);


    }
}
