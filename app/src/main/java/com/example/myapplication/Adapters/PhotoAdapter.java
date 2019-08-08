package com.example.myapplication.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Activities.EventDetail;
import com.example.myapplication.Activities.PictureDetail;
import com.example.myapplication.Models.Picture;
import com.example.myapplication.Models.UserNode;
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
        private TextView tvDisplayName;
        private StorageReference mStorageRef;
        private Uri url;


        public ViewHolder(View itemView) {
            super(itemView);
            ivEventPicture = (ImageView) itemView.findViewById(R.id.ivEventPicture);
            tvDisplayName = (TextView) itemView.findViewById(R.id.tvDisplayName);
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

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("UserNodes").child(picture.user);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserNode user = dataSnapshot.getValue(UserNode.class);
                    tvDisplayName.setText(user.name);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("PhotoAdapter", "Error reading database", databaseError.toException());
                }
            });
        }

        @Override
        public void onClick(View v) {

            //gets item position
            int position = getAdapterPosition();
            // make sure the position is valid
            if (position != RecyclerView.NO_POSITION) {
                // get the movie at the position
                Picture picture = pictures.get(position);

                // create intent for new activity
                Intent intent = new Intent(context, PictureDetail.class);
                intent.putExtra(Picture.class.getSimpleName(), Parcels.wrap(picture));
                // show the activity
                context.startActivity(intent);
            }
        }

    }

}