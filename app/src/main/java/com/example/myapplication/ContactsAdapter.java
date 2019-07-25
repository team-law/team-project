package com.example.myapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.Models.Contact;

import java.util.ArrayList;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    private ArrayList<Contact> contacts;

    public ContactsAdapter(ArrayList<Contact> contacts) { this.contacts = contacts; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_contact, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Contact contact = contacts.get(i);
        viewHolder.bind(contact);
    }

    @Override
    public int getItemCount() { return contacts.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvContactName;
        private TextView tvPhoneNumber;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvContactName = itemView.findViewById(R.id.tvContactName);
            tvPhoneNumber = itemView.findViewById(R.id.tvPhoneNumber);
        }

        public void bind(Contact contact) {
            tvContactName.setText(contact.getName());
            tvPhoneNumber.setText(contact.getPhoneNumber());
        }
    }
}
