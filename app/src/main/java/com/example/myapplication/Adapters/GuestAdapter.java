package com.example.myapplication.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;

import java.util.ArrayList;

public class GuestAdapter extends RecyclerView.Adapter<GuestAdapter.ViewHolder> {

    private ArrayList<String> guests;

    public GuestAdapter(ArrayList<String> guests) {
        this.guests = guests;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_guest, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String guest = guests.get(i);
        viewHolder.bind(guest);
    }

    @Override
    public int getItemCount() {
        return guests.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvGuestName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGuestName = itemView.findViewById(R.id.tvGuestName);
        }

        public void bind(String guest) {
            tvGuestName.setText(guest);
        }
    }
}
