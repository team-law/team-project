package com.example.myapplication.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.Activities.EventDetail;
import com.example.myapplication.Adapters.ProfileAdapter;
import com.example.myapplication.Models.Event;
import com.example.myapplication.Models.Picture;
import com.example.myapplication.Models.UserNode;
import com.example.myapplication.R;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.common.util.Strings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.snapshot.StringNode;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.blox.graphview.Algorithm;
import de.blox.graphview.BaseGraphAdapter;
import de.blox.graphview.Graph;
import de.blox.graphview.GraphView;
import de.blox.graphview.Node;
import de.blox.graphview.ViewHolder;
import de.blox.graphview.energy.FruchtermanReingoldAlgorithm;
import de.blox.graphview.layered.SugiyamaAlgorithm;
import de.blox.graphview.layered.SugiyamaConfiguration;
import de.blox.graphview.tree.BuchheimWalkerAlgorithm;
import de.blox.graphview.tree.BuchheimWalkerConfiguration;

import static android.net.Uri.parse;

public class EventMapViewFragment extends Fragment {

    private Event event;
    private HashMap<String, String> attending;
    private HashMap<String, Node> nodes;
    private Graph graph;
    private int nodeCount = 1;
    GraphView graphView;
    private List<Picture> mPictures;
    private RecyclerView rvPopupPics;
    private ProfileAdapter adapter;

    // Initialize database
    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        graphView = view.findViewById(R.id.graph);

        event = getArguments().getParcelable("event");
        attending = (HashMap<String, String>) getArguments().getSerializable("attending");
        nodes = new HashMap<>();

        // example tree
        graph = new Graph();
        createNodes();

        // you can set the graph via the constructor or use the adapter.setGraph(Graph) method
        final BaseGraphAdapter<ViewHolder> adapter = new BaseGraphAdapter<ViewHolder>(graph) {
//            private ImageView ivNodePic;

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.node, parent, false);
//                ivNodePic = parent.findViewById(R.id.ivNodePic);
                return new SimpleViewHolder(view);
            }

            @Override
            public void onBindViewHolder(ViewHolder viewHolder, Object data, int position) {
//                ((SimpleViewHolder)viewHolder).textView.setText(data.toString());
//                Uri profilePic = Uri.parse(data.toString());
                ImageView ivNodePic = ((SimpleViewHolder)viewHolder).ivNodePic;
                UserNode clicked = (UserNode) data;
                Glide.with(EventMapViewFragment.this).load(Uri.parse(clicked.profilePic)).into(ivNodePic);
//                ((SimpleViewHolder)viewHolder).ivNodePic.setImageURI(Profile.getCurrentProfile().getProfilePictureUri(100, 100));
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Node n = getNode(position);
                       showPopup(v, n);
                    }
                });

            }

            @Override
            public Node getNode(int position) {
                return graph != null ? graph.getNode(position) : null;
            }


        };
        graphView.setAdapter(adapter);

        // set the algorithm here
        final BuchheimWalkerConfiguration configuration = new BuchheimWalkerConfiguration.Builder()
                .setSiblingSeparation(100)
                .setLevelSeparation(300)
                .setSubtreeSeparation(300)
                .setOrientation(BuchheimWalkerConfiguration.ORIENTATION_TOP_BOTTOM)
                .build();
        adapter.setAlgorithm(new BuchheimWalkerAlgorithm(configuration));
    }


    //popup list of pictures taken by user from the event
    public void showPopup(View anchorView, Node clicked) {

        View popupView = getLayoutInflater().inflate(R.layout.user_pics_popup, null);

        PopupWindow popupWindow = new PopupWindow(popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        rvPopupPics = popupView.findViewById(R.id.rvPopupPics);

        //setting up the adapter and recycler view
        mPictures = new ArrayList<>();
        adapter = new ProfileAdapter(getContext(), mPictures);
        rvPopupPics.setAdapter(adapter);
        rvPopupPics.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        queryPosts((UserNode) clicked.getData());

        // If the PopupWindow should be focusable
        popupWindow.setFocusable(true);

        // If you need the PopupWindow to dismiss when when touched outside
        popupWindow.setBackgroundDrawable(new ColorDrawable());

        int location[] = new int[2];

        // Get the View's(the one that was clicked in the Fragment) location
        anchorView.getLocationOnScreen(location);

        //display the PopUp
        popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY, location[0], location[1] + anchorView.getHeight());

    }


    //want to get only the pictures from that person from that event
    //go through allPictures list of the node that is clicked on
    //if the picture value matches the event access code then add pic to adapter
    protected void queryPosts(UserNode clickedNode) {
        DatabaseReference ref = database.getReference();


        ref.addValueEventListener(new ValueEventListener() {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //UserNode userInfo = dataSnapshot.child("UserNodes").child(us.getUid()).getValue(UserNode.class);
                Map<String, String> userPics = new HashMap<>(1);
                ArrayList<String> eventPics = new ArrayList<>(1);
                userPics = clickedNode.picturesTaken; //gets all the pictures the user has taken

                //for every picture the user has taken if the value matches the event value then load the picture
                for (String key: userPics.keySet()) {
                    if (userPics.get(key).equals(event.accessCode)) {
                        eventPics.add(key);
                    }
                }

                for(DataSnapshot picSnapshot: dataSnapshot.child("Picture").getChildren()){
                    Picture picture = picSnapshot.getValue(Picture.class);
                    String key = picSnapshot.getKey();

                    if (eventPics.contains(key) && !mPictures.contains(picture)) { //code from the push
                        mPictures.add(0, picture);
                        adapter.notifyDataSetChanged();
                    }
                    //swipeContainer.setRefreshing(false);
                }
                //Log.d(TAG, "loaded events correctly");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }

    private void createNodes() {
        final DatabaseReference reference = database.getReference().child("UserNodes");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (String user: attending.keySet()) {
                    // Get guest user
                    UserNode guest = dataSnapshot.child(user).getValue(UserNode.class);
                    Node guestNode;
                    // Check that guest doesn't already have a node
                    if (nodes.containsKey(guest.userId)) {
                        guestNode = nodes.get(guest.userId);
                    } else {
                        guestNode = new Node(guest);
                        nodes.put(guest.userId, guestNode);
                    }
                    // Check that guest didn't invite itself (isn't host)
                    if (!user.equals(event.host)) {
                        // Get user who invited guest
                        UserNode invitedBy = dataSnapshot.child(attending.get(user)).getValue(UserNode.class);
                        Node invitedByNode;
                        // if the node has already been created
                        if (nodes.containsKey(invitedBy.userId)) {
                            invitedByNode = nodes.get(invitedBy.userId);
                        } else {
                            invitedByNode = new Node(invitedBy);
                            nodes.put(invitedBy.userId, invitedByNode);
                        }
                        graph.addEdge(invitedByNode, guestNode);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MapView", "The read failed: " + databaseError.getMessage());
            }
        });
    }

    private String getNodeText() {
        return "Node " + nodeCount++;
    }

    class SimpleViewHolder extends ViewHolder{
//        TextView textView;
        ImageView ivNodePic;

        SimpleViewHolder(View itemView) {
            super(itemView);
//            textView = itemView.findViewById(R.id.textView);
            ivNodePic = itemView.findViewById(R.id.ivNodePic);
        }
    }
}
