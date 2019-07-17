package com.example.myapplication.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class Fragment {
    // onCreateView method is called when Fragment should create its view object hierarchy,
    // either dynamically or via XML layout inflation
    @Nullable
    public abstract View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    // This event is triggered soon after onCreteView()
    // Any view setup should occur here
    public abstract void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState);
}
