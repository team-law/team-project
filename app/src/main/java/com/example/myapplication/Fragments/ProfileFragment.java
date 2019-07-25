package com.example.myapplication.Fragments;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
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
import com.example.myapplication.FirebaseApp;
import com.example.myapplication.HomeActivity;
import com.example.myapplication.LoginActivity;
import com.example.myapplication.Models.Event;
import com.example.myapplication.Models.Picture;
import com.example.myapplication.Models.UserNode;
import com.example.myapplication.ProfileAdapter;
import com.example.myapplication.R;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    private Button btnLogout;
    private ImageView ivProfilePicture;
    private TextView tvUsername;
    private TextView tvEmail;
    private TextView tvNumFriends;
    private TextView tvFriendList;
    private RecyclerView rvProfilePosts;
    private ProfileAdapter adapter;
    private List<Picture> mPictures;

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

        // Initialize objects in profile fragment
        ivProfilePicture = (ImageView) view.findViewById(R.id.ivProfilePicture);
        tvUsername = (TextView) view.findViewById(R.id.tvUsername);
        tvEmail = (TextView) view.findViewById(R.id.tvEmail);
        tvNumFriends = (TextView) view.findViewById(R.id.tvNumFriends);
        tvFriendList = (TextView) view.findViewById(R.id.tvFriendList);
        rvProfilePosts = view.findViewById(R.id.rvProfilePosts);

        //setting up the adapter and recycler view
        mPictures = new ArrayList<>();
        adapter = new ProfileAdapter(getContext(), mPictures);
        rvProfilePosts.setAdapter(adapter);
        rvProfilePosts.setLayoutManager(new LinearLayoutManager(getContext()));
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
                Map<String, Picture> userPics = new HashMap<>(1);
                userPics = userInfo.picturesTaken; //gets all the pictures the user has taken

                for(DataSnapshot picSnapshot: dataSnapshot.child("Picture").getChildren()){
                    Picture picture = picSnapshot.getValue(Picture.class);
                    String key = picSnapshot.getKey();

                    if (userPics.containsKey(key)) { //code from the push
                        mPictures.add(picture);
                        adapter.notifyDataSetChanged();
                    }
                    //swipeContainer.setRefreshing(false);
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
        Glide.with(this).load(profile.getProfilePictureUri(400, 400)).into(ivProfilePicture);
//        tvUsername.setText(profile.getName());
//        Glide.with(this).load(user.getPhotoUrl()).into(ivProfilePicture);
        tvUsername.setText(user.getDisplayName());
        tvEmail.setText(user.getEmail());
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            Toast.makeText(getContext(), "Successfully queried fb friends", Toast.LENGTH_LONG).show();
                            JSONArray friendList = object.getJSONObject("friends").getJSONArray("data");
                            tvNumFriends.setText("Friends: " + friendList.length());
                            for (int i = 0; i < friendList.length(); i++) {
                                tvFriendList.append(friendList.getJSONObject(i).getString("name"));
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getContext(), "Error parsing json", Toast.LENGTH_LONG).show();
                            Log.e(TAG, "Error parsing json", e);
                            e.printStackTrace();
                        }

                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "friends");
        request.setParameters(parameters);
        request.executeAsync();
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
