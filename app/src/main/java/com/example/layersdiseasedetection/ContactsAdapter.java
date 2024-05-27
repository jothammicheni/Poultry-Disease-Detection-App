package com.example.layersdiseasedetection;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.layersdiseasedetection.data.UserDetails;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    private List<UserDetails> userList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(UserDetails userDetails);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ContactsAdapter(List<UserDetails> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_rv_resource, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserDetails userDetails = userList.get(position);
        holder.bind(userDetails);
        holder.itemView.setOnClickListener(view -> {
            if (listener != null) {
                listener.onItemClick(userDetails);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textName,textEmail,textPhone;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.username);
            textEmail=itemView.findViewById(R.id.TVemail);
            textPhone=itemView.findViewById(R.id.TVnumber);
        }

        public void bind(UserDetails userDetails) {

            textName.setText(userDetails.getUsername() +"("+userDetails.getUserphone()+")");
            textEmail.setText(userDetails.getUseremail());
          //  textPhone.setText(userDetails.getUserphone());
        }
    }
}
