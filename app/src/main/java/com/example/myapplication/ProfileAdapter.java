package com.example.myapplication;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Models.Picture;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder>{

    private Context context;
    private List<Picture> pictures;

    public ProfileAdapter(Context context, List<Picture> pictures) {
        this.context = context;
        this.pictures = pictures;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_picture_album_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Picture picture = pictures.get(i);
        viewHolder.bind(picture);
    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }

    public void clear() {
        pictures.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Picture> list) {
        pictures.addAll(list);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivEventPicture;
        private StorageReference mStorageRef;
        private Uri url;

        public ViewHolder(View itemView) {
            super(itemView);
            ivEventPicture = itemView.findViewById(R.id.ivEventPicture);
        }

        public void bind (Picture picture) {
            //TODO finish this - load the picture in
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
        }

    }
}
