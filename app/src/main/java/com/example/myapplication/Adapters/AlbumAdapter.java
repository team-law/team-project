package com.example.myapplication.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
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

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AlbumAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static final int EVENT_TYPE = 1;
    static final int LIST_TYPE = 0;

    private Context context;
    private List<Event> events;
    boolean isContainSubList = false;
    boolean entered = false;
    private List<Event> subListEvents;
    int itemLayoutRes;

    public AlbumAdapter (Context context, List<Event> events, int itemLayoutRes) {
        this.context = context;
        this.events = events;
        this.itemLayoutRes = itemLayoutRes;
    }

    public void setSubList(List<Event> subListEvents) {
        this.subListEvents = subListEvents;
        this.isContainSubList = true;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && isContainSubList) {
            return LIST_TYPE;
        }
        return EVENT_TYPE;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == EVENT_TYPE) {
            View view = LayoutInflater.from(context).inflate(itemLayoutRes, parent, false);
            return new EventViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
            return new ListViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if (getItemViewType(i) == EVENT_TYPE) {
            Event event = events.get(isContainSubList ? i-1 : i);
            ((EventViewHolder) viewHolder).bind(event);
        }
        else {
            ((ListViewHolder) viewHolder).bindEvents(subListEvents);
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        events.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void set(List<Event> list) {
        events.clear();
        events.addAll(list);
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Event> list) {
        events.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return events.size() + (isContainSubList ? 1 : 0);
    }

    class ListViewHolder extends RecyclerView.ViewHolder {
        RecyclerView rvUpcomingEvents;
        AlbumAdapter upcomingEventAdapter;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            rvUpcomingEvents = (RecyclerView) itemView;
            rvUpcomingEvents.setMotionEventSplittingEnabled(true);
            rvUpcomingEvents.setLayoutManager(new LinearLayoutManager(rvUpcomingEvents.getContext(), LinearLayoutManager.HORIZONTAL, false));
            upcomingEventAdapter = new AlbumAdapter(rvUpcomingEvents.getContext(), new ArrayList<>(), R.layout.item_small_album);
            rvUpcomingEvents.setAdapter(upcomingEventAdapter);
        }

        public void bindEvents(List<Event> events) {
            upcomingEventAdapter.set(events);
        }
    }

    class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        // helper variables to prevent double clicking
        private static final long CLICK_TIME_INTERVAL = 300;
        private long mLastClickTime = System.currentTimeMillis();

        private ImageView ivAlbumPicture;
        private TextView tvTitle;
        private TextView tvHost;
        private TextView tvDateTime;
        private StorageReference mStorageRef;
        private Uri url;
        private CardView card;
        final FirebaseDatabase database = FirebaseDatabase.getInstance();


        public EventViewHolder(View itemView){
            super(itemView);
            ivAlbumPicture = (ImageView) itemView.findViewById(R.id.ivAlbumPicture);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvDateTime = (TextView) itemView.findViewById(R.id.tvDateTime);
            tvHost = (TextView) itemView.findViewById(R.id.tvHostName);
            card = (CardView) itemView.findViewById(R.id.cardView2);
            itemView.setOnClickListener(this);

        }

        public void bind(final Event event) {
            entered = false;
            tvTitle.setText(event.title);

            if(event.hostName != null) {
                tvHost.setText(event.hostName);
            }

            tvDateTime.setText(getDate(event.date)+ "       " + event.time);

            
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
                entered = true;
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
            } else if (!entered){
                ivAlbumPicture.setImageResource(android.R.color.transparent);
            }

        }

        private String getDate(String time) {
            String date = "";
            String year = time.substring(0,4);
            int month = Integer.parseInt(time.substring(4, 6)) - 1;
            String day = time.substring(6, 8);

            String m = "";
            DateFormatSymbols dfs = new DateFormatSymbols();
            String[] months = dfs.getMonths();
            if (month >= 0 && month <= 11 ) {
                m = months[month];
            }
            date += m + " " + day + ", " + year;
            return date;
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
                    url = null;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    exception.printStackTrace();
                }
            });
            mStorageRef = null;
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
