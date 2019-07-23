package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Models.Event;

import org.parceler.Parcels;

import java.util.List;

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

        private ImageView ivAlbumPicture;
        private TextView tvTitle;
        private TextView tvHost;


        public ViewHolder(View itemView){
            super(itemView);
            ivAlbumPicture = (ImageView) itemView.findViewById(R.id.ivAlbumPicture);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvHost = (TextView) itemView.findViewById(R.id.tvHostName);
            itemView.setOnClickListener(this);

        }

        public void bind(final Event event) {
            tvTitle.setText(event.title);
            // TODO -- add ivAlbumPicture
            // TODO -- pass in user ID from createEvent and call it here

            /*
            if(image != null) {
                Glide.with(context)
                        .load(image.getUrl())
                        .into(ivImage);
            }
            */
        }

        @Override
        public void onClick(View v) {
            //gets item position
            int position = getAdapterPosition();
            // make sure the position is valid
            if (position != RecyclerView.NO_POSITION) {
                // get the movie at the position
                Event event = events.get(position);

                // create intent for new activity
                Intent intent = new Intent(context, EventDetail.class);
                intent.putExtra("event", Parcels.wrap(event));
                // show the activity
                 context.startActivity(intent);
            }
        }
    }

}
