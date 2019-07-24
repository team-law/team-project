//package com.example.myapplication;
//
//import android.content.Context;
//import android.provider.ContactsContract;
//import android.support.annotation.NonNull;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import java.util.List;
//
//public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
//
//    private Context context;
//    private List<ContactsContract.Contacts> contacts;
//
//    public ContactsAdapter(List<Contact> contacts) { this.contacts = contacts; }
//
//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//        View view = LayoutInflater.from(context).inflate(R.layout.item_contact, viewGroup, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
//        ContactsContract.Contacts contact = contacts.get(i);
//        viewHolder.bind(contact);
//    }
//
//    @Override
//    public int getItemCount() { return contacts.size(); }
//
//    class ViewHolder extends RecyclerView.ViewHolder {
//
//        private TextView tvContactName;
//        private TextView tvPhoneNumber;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            tvContactName = itemView.findViewById(R.id.tvContactName);
//            tvPhoneNumber = itemView.findViewById(R.id.tvPhoneNumber);
//        }
//
//        public void bind(ContactsContract.Contacts contact) {
//            tvContactName.setText(contact.);
//            tvPhoneNumber.setText(context.getPhoneNumber());
//        }
//    }
//}
