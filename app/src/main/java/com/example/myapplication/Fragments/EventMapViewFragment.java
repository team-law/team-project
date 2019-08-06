package com.example.myapplication.Fragments;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.Models.Event;
import com.example.myapplication.Models.UserNode;
import com.example.myapplication.R;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.common.util.Strings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.snapshot.StringNode;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

//import de.blox.graphview.BaseGraphAdapter;
//import de.blox.graphview.Graph;
//import de.blox.graphview.GraphView;
//import de.blox.graphview.Node;
//import de.blox.graphview.ViewHolder;
//import de.blox.graphview.energy.FruchtermanReingoldAlgorithm;
//import de.blox.graphview.layered.SugiyamaAlgorithm;
//import de.blox.graphview.layered.SugiyamaConfiguration;
//import de.blox.graphview.tree.BuchheimWalkerAlgorithm;
//import de.blox.graphview.tree.BuchheimWalkerConfiguration;

public class EventMapViewFragment extends Fragment {

//    private Event event;
//    private HashMap<String, String> attending;
//    private HashMap<String, Node> nodes;
//    private Graph graph;
//    private int nodeCount = 1;
//
//    // Initialize database
//    final FirebaseDatabase database = FirebaseDatabase.getInstance();
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_event_map, container, false);
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        GraphView graphView = view.findViewById(R.id.graph);
//
//        event = getArguments().getParcelable("event");
//        attending = (HashMap<String, String>) getArguments().getSerializable("attending");
//        nodes = new HashMap<>();
//
//        // example tree
//        graph = new Graph();
//        createNodes();
//
//        // you can set the graph via the constructor or use the adapter.setGraph(Graph) method
//        final BaseGraphAdapter<ViewHolder> adapter = new BaseGraphAdapter<ViewHolder>(graph) {
//
//            @NonNull
//            @Override
//            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//                final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.node, parent, false);
//                return new SimpleViewHolder(view);
//            }
//
//            @Override
//            public void onBindViewHolder(ViewHolder viewHolder, Object data, int position) {
//                ((SimpleViewHolder)viewHolder).textView.setText(data.toString());
////                ((SimpleViewHolder)viewHolder).ivNodePic.setImageURI();
//
//            }
//        };
//        graphView.setAdapter(adapter);
//
//        // set the algorithm here
//        final BuchheimWalkerConfiguration configuration = new BuchheimWalkerConfiguration.Builder()
//                .setSiblingSeparation(100)
//                .setLevelSeparation(300)
//                .setSubtreeSeparation(300)
//                .setOrientation(BuchheimWalkerConfiguration.ORIENTATION_TOP_BOTTOM)
//                .build();
//        adapter.setAlgorithm(new BuchheimWalkerAlgorithm(configuration));
//    }
//
//    private void createNodes() {
//        final DatabaseReference reference = database.getReference().child("UserNodes");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (String user: attending.keySet()) {
//                    // Get guest user
//                    UserNode guest = dataSnapshot.child(user).getValue(UserNode.class);
//                    Node guestNode;
//                    // Check that guest doesn't already have a node
//                    if (nodes.containsKey(guest.name)) {
//                        guestNode = nodes.get(guest.name);
//                    } else {
//                        guestNode = new Node(guest.name);
//                        nodes.put(guest.name, guestNode);
//                    }
//                    // Check that guest didn't invite itself (isn't host)
//                    if (!user.equals(event.host)) {
//                        // Get user who invited guest
//                        UserNode invitedBy = dataSnapshot.child(attending.get(user)).getValue(UserNode.class);
//                        Node invitedByNode;
//                        // if the node has already been created
//                        if (nodes.containsKey(invitedBy.name)) {
//                            invitedByNode = nodes.get(invitedBy.name);
//                        } else {
//                            invitedByNode = new Node(invitedBy.name);
//                            nodes.put(invitedBy.name, invitedByNode);
//                        }
//                        graph.addEdge(invitedByNode, guestNode);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.e("MapView", "The read failed: " + databaseError.getMessage());
//            }
//        });
//    }
//
//    private String getNodeText() {
//        return "Node " + nodeCount++;
//    }
//
//    class SimpleViewHolder extends ViewHolder {
//        TextView textView;
//        ImageView ivNodePic;
//
//        SimpleViewHolder(View itemView) {
//            super(itemView);
//            textView = itemView.findViewById(R.id.textView);
//            ivNodePic = itemView.findViewById(R.id.ivNodePic);
//        }
//    }
}
