package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapplication.Models.Contact;

import java.util.ArrayList;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    private ArrayList<Contact> contacts;
    private ArrayList<String> invited;

    public ContactsAdapter(ArrayList<Contact> contacts) {
        this.contacts = contacts;
        this.invited = new ArrayList<>();
    }

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

    public ArrayList<String> getInvitedList() { return invited; }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Contact contact;
        private TextView tvContactName;
        private TextView tvPhoneNumber;
        private ConstraintLayout clContact;
        private boolean rowSelected = false;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvContactName = itemView.findViewById(R.id.tvContactName);
            tvPhoneNumber = itemView.findViewById(R.id.tvPhoneNumber);
            clContact = itemView.findViewById(R.id.clContanct);

            itemView.setOnClickListener(this);
        }

        public void bind(Contact contact) {
            this.contact = contact;
            tvContactName.setText(contact.getName());
            tvPhoneNumber.setText(contact.getPhoneNumber());
            if (contact.isSelected()) {
                setRowSelected();
            } else {
                setRowDefault();
            }
        }

        public void setRowSelected() {
            tvContactName.setTextColor(Color.WHITE);
            tvPhoneNumber.setTextColor(Color.WHITE);
            clContact.setBackgroundResource(R.color.grapefruitPink);
        }

        public void setRowDefault() {
            tvContactName.setTextColor(Color.BLACK);
            tvPhoneNumber.setTextColor(Color.BLACK);
            clContact.setBackgroundColor(Color.WHITE);
        }

        @Override
        public void onClick(View v) {
            if (contact.isSelected()) {
                invited.remove(tvPhoneNumber.getText().toString());
                setRowDefault();
            } else {
                invited.add(tvPhoneNumber.getText().toString());
                setRowSelected();
            }
            contact.toggleSelected();
        }
    }
}
