package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Models.Picture;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {
    private Context context;
    private List<Picture> pictures;

    public PhotoAdapter(Context context, List<Picture> pictures) {
        this.context = context;
        this.pictures = pictures;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_picture_album_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Picture picture = pictures.get(i);
        viewHolder.bind(picture);

    }

    // Clean all elements of the recycler
    public void clear() {
        pictures.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Picture> list) {
        pictures.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ivEventPicture;
        private StorageReference mStorageRef;
        private Uri url;


        public ViewHolder(View itemView) {
            super(itemView);
            ivEventPicture = (ImageView) itemView.findViewById(R.id.ivEventPicture);
            itemView.setOnClickListener(this);

        }

        public void bind(final Picture picture) {

            String imgRef = picture.imageRef;

            mStorageRef = FirebaseStorage.getInstance().getReference("Images");
            StorageReference storageRef = mStorageRef.child(imgRef);

            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                   url = uri;
                    Glide.with(context)
                            .load(url)
                            .into(ivEventPicture);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    exception.printStackTrace();
                }
            });

            // TODO -- add ivEventPicture

//            if(picture != null) {
//                Glide.with(context)
//                        .load(url)
//                        .into(ivEventPicture);
//            }

        }

        @Override
        public void onClick(View v) {
/*
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
            */
        }

    }

}