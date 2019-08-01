package com.example.myapplication.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.Serializable;

import com.bumptech.glide.Glide;
import com.example.myapplication.Activities.EventDetail;
import com.example.myapplication.Models.Event;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.parceler.Parcels;

import java.util.List;
import java.util.Map;

public class AlbumAdapter  extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    private Context context;
    private List<Event> events;

    public AlbumAdapter (Context context, List<Event> events) {
        this.context = context;
        this.events = events;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_album, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Event event = events.get(i);
        viewHolder.bind(event);
    }

    // Clean all elements of the recycler
    public void clear() {
        events.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Event> list) {
        events.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        // helper variables to prevent double clicking
        private static final long CLICK_TIME_INTERVAL = 300;
        private long mLastClickTime = System.currentTimeMillis();

        private ImageView ivAlbumPicture;
        private TextView tvTitle;
        private TextView tvHost;
        private StorageReference mStorageRef;
        private Uri url;
        private CardView card;
        final FirebaseDatabase database = FirebaseDatabase.getInstance();


        public ViewHolder(View itemView){
            super(itemView);
            ivAlbumPicture = (ImageView) itemView.findViewById(R.id.ivAlbumPicture);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvHost = (TextView) itemView.findViewById(R.id.tvHostName);
            card = (CardView) itemView.findViewById(R.id.cardView2);
            itemView.setOnClickListener(this);

        }

        public void bind(final Event event) {
            tvTitle.setText(event.title);
            // TODO -- add ivAlbumPicture
            if(event.hostName != null) {
                tvHost.setText(event.hostName);
            }

            
            //ivAlbumPicture

            // call network to get imgRef of first picture

            //CardView card = new CardView(context);
            if (event.passed) {
                //if the event is in the past, switch the card view to be pink
                card.setCardBackgroundColor(context.getResources().getColor(R.color.grapefruitPink));
            } else {
                card.setCardBackgroundColor(context.getResources().getColor(R.color.dustyYellow));
            }

            if(event.allPictures.size() >= 1) {
                // get imgID of first picture
                Map.Entry<String, Boolean> entry = event.allPictures.entrySet().iterator().next();

                String imgID = entry.getKey();

                // get imgRef of first picture
                DatabaseReference ref = database.getReference("Picture/" + imgID + "/imageRef");

                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String imgRef = dataSnapshot.getValue(String.class);
                        getImage(imgRef);
                        imgRef = "";

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());
                    }

                });



            /*
            if(image != null) {
                Glide.with(context)
                        .load(image.getUrl())
                        .into(ivAlbumPicture);
            }*/
            }

        }

        private void getImage(String imgRef){

            mStorageRef = FirebaseStorage.getInstance().getReference("Images");
            StorageReference storageRef = mStorageRef.child(imgRef);

            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    url = uri;
                    Glide.with(context)
                            .load(url)
                            .into(ivAlbumPicture);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    exception.printStackTrace();
                }
            });
        }

        @Override
        public void onClick(View v) {
            // prevent double clicks
            long now = System.currentTimeMillis();
            if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
                return;
            }
            mLastClickTime = now;

            //gets item position
            int position = getAdapterPosition();
            // make sure the position is valid
            if (position != RecyclerView.NO_POSITION) {
                // get the movie at the position
                Event event = events.get(position);

                // create intent for new activity
                Intent intent = new Intent(context, EventDetail.class);
                intent.putExtra("attending", (Serializable) event.attending);
                intent.putExtra("allPictures", (Serializable) event.allPictures);
                intent.putExtra("event", Parcels.wrap(event));
                // show the activity
                context.startActivity(intent);
            }
        }
    }

}
