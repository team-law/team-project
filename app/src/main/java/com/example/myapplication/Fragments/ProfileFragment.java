package com.example.myapplication.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.Activities.LoginActivity;
import com.example.myapplication.Models.Picture;
import com.example.myapplication.Models.UserNode;
import com.example.myapplication.Adapters.ProfileAdapter;
import com.example.myapplication.R;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private Button btnLogout;
    private ImageView ivProfilePicture;
    private TextView tvUsername;
    private RecyclerView rvProfilePosts;
    private ProfileAdapter adapter;
    private List<Picture> mPictures;

    private SwipeRefreshLayout swipeContainer;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Profile profile;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    // This event is triggered soon after onCreteView()
    // Any view setup should occur here
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize Firebase Auth and User
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        profile = Profile.getCurrentProfile();
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // Initialize objects in profile fragment
        ivProfilePicture = (ImageView) view.findViewById(R.id.ivProfilePicture);
        tvUsername = (TextView) view.findViewById(R.id.tvUsername);
//        tvEmail = (TextView) view.findViewById(R.id.tvEmail);
//        tvNumFriends = (TextView) view.findViewById(R.id.tvNumFriends);
//        tvFriendList = (TextView) view.findViewById(R.id.tvFriendList);
        rvProfilePosts = view.findViewById(R.id.rvProfilePosts);

        //setting up the adapter and recycler view
        mPictures = new ArrayList<>();
        adapter = new ProfileAdapter(getContext(), mPictures);
        rvProfilePosts.setAdapter(adapter);
        rvProfilePosts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        queryPosts();

        btnLogout = (Button) view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth .signOut();
                LoginManager.getInstance().logOut();
                updateUI();
            }
        });
        queryUserInfo();

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                adapter.clear();
                mPictures.clear();
                queryPosts();

            }
        });
    }

    //get all the pictures that are taken by the user
    protected void queryPosts() {
        DatabaseReference ref = database.getReference();

        ref.addValueEventListener(new ValueEventListener() {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserNode userInfo = dataSnapshot.child("UserNodes").child(user.getUid()).getValue(UserNode.class);
                Map<String, String> userPics = new HashMap<>(1);
                userPics = userInfo.picturesTaken; //gets all the pictures the user has taken

                for(DataSnapshot picSnapshot: dataSnapshot.child("Picture").getChildren()){
                    Picture picture = picSnapshot.getValue(Picture.class);
                    String key = picSnapshot.getKey();

                    if (userPics.containsKey(key) && !mPictures.contains(picture)) { //code from the push
                        mPictures.add(0, picture);
                        adapter.notifyDataSetChanged();
                    }
                    swipeContainer.setRefreshing(false);
                }
                Log.d(TAG, "loaded events correctly");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }

    private void queryUserInfo() {
        // Fetch Facebook information and to insert in objects
        Glide.with(this).load(profile.getProfilePictureUri(400, 400)).apply(RequestOptions.circleCropTransform()).into(ivProfilePicture);
//        tvUsername.setText(profile.getName());
//        Glide.with(this).load(user.getPhotoUrl()).into(ivProfilePicture);
        tvUsername.setText(user.getDisplayName());
//        tvEmail.setText(user.getEmail());
//        GraphRequest request = GraphRequest.newMeRequest(
//                AccessToken.getCurrentAccessToken(),
//                new GraphRequest.GraphJSONObjectCallback() {
//                    @Override
//                    public void onCompleted(JSONObject object, GraphResponse response) {
//                        try {
//                            Toast.makeText(getContext(), "Successfully queried fb friends", Toast.LENGTH_LONG).show();
//                            JSONArray friendList = object.getJSONObject("friends").getJSONArray("data");
//                            tvNumFriends.setText("Friends: " + friendList.length());
//                            for (int i = 0; i < friendList.length(); i++) {
//                                tvFriendList.append(friendList.getJSONObject(i).getString("name"));
//                            }
//                        } catch (JSONException e) {
//                            Toast.makeText(getContext(), "Error parsing json", Toast.LENGTH_LONG).show();
//                            Log.e(TAG, "Error parsing json", e);
//                            e.printStackTrace();
//                        }
//
//                    }
//                });

//        Bundle parameters = new Bundle();
//        parameters.putString("fields", "friends");
//        request.setParameters(parameters);
//        request.executeAsync();
    }

    private void updateUI() {
        Toast.makeText(getContext(), "You are logged out!", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        // check if user was already logged-out (user persistence)
        if (mAuth.getCurrentUser() == null) {
            updateUI();
        }
    }

}
