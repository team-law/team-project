package com.example.myapplication.Adapters;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.Models.Contact;
import com.example.myapplication.Models.UserNode;
import com.example.myapplication.R;

import java.util.ArrayList;

public class GuestAdapter extends RecyclerView.Adapter<GuestAdapter.ViewHolder> {

    private ArrayList<UserNode> guests;
    private Context context;

    public GuestAdapter(Context context, ArrayList<UserNode> guests) {
        this.guests = guests;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_guest, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        UserNode guest = guests.get(i);
        viewHolder.bind(guest);
    }

    @Override
    public int getItemCount() {
        return guests.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvGuestName;
        private ImageView ivGuestPic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGuestName = itemView.findViewById(R.id.tvGuestName);
            ivGuestPic = itemView.findViewById(R.id.ivGuestPic);
        }

        public void bind(UserNode guest) {
            tvGuestName.setText(guest.name);
            Glide.with(context).load(Uri.parse(guest.profilePic)).apply(RequestOptions.circleCropTransform()).into(ivGuestPic);
        }
    }
}
