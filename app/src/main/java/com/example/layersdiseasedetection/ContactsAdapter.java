package com.example.layersdiseasedetection;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.layersdiseasedetection.data.UserDetails;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.newusers_recycler_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserDetails userDetails = userList.get(position);
        holder.bind(userDetails);

        // Log latitude and longitude
        Log.d("ContactsAdapter", "Latitude: " + userDetails.getLatitude());
        Log.d("ContactsAdapter", "Longitude: " + userDetails.getLongitude());

        holder.BtnWhatssap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.openWhatsAppChat(userDetails);
            }
        });

        holder.btvVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), NewUserDetailsConfirmation.class);
                intent.putExtra("username", userDetails.getUsername());
                intent.putExtra("email", userDetails.getUseremail());
                intent.putExtra("phone", userDetails.getUserphone());
                intent.putExtra("city", userDetails.getUserCity());
                intent.putExtra("password", userDetails.getUserpassword());
                intent.putExtra("latitude", userDetails.getLatitude());
                intent.putExtra("longitude", userDetails.getLongitude());
                v.getContext().startActivity(intent);
            }
        });

        holder.btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference contactsRef = FirebaseDatabase.getInstance().getReference("newUserDetails");
                contactsRef.child(userDetails.getUseremail().replace(".", ",")).removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(v.getContext(), "User deleted successfully", Toast.LENGTH_SHORT).show();
                        userList.remove(position);
                        notifyItemRemoved(position);
                    } else {
                        Toast.makeText(v.getContext(), "Failed to delete user", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(userDetails);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textName, textEmail, textPhone;
        private LinearLayout LLUserHolders;
        private ImageButton BtnWhatssap;
        private AppCompatButton btvVerify, btndelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.username);
            textEmail = itemView.findViewById(R.id.TVemail);
            textPhone = itemView.findViewById(R.id.TVnumber);
            LLUserHolders = itemView.findViewById(R.id.LLUserHolder);
            BtnWhatssap = itemView.findViewById(R.id.BtnWhatssap);
            btvVerify = itemView.findViewById(R.id.btvVerify);
            btndelete = itemView.findViewById(R.id.btndelete);

            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) LLUserHolders.getLayoutParams();
            layoutParams.topMargin = 5;
            layoutParams.bottomMargin = 5;
            layoutParams.leftMargin = 5;
            layoutParams.rightMargin = 5;
            LLUserHolders.setLayoutParams(layoutParams);
        }

        public void bind(UserDetails userDetails) {
            textName.setText(userDetails.getUsername() + " (" + userDetails.getUserphone() + ")");
            textEmail.setText(userDetails.getUseremail());
            textPhone.setText(userDetails.getUserphone());
        }

        public void openWhatsAppChat(UserDetails userDetails) {
            Context context = itemView.getContext();
            String phoneNumber = userDetails.getUserphone();

            try {
                Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + phoneNumber);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                if (intent.resolveActivity(context.getPackageManager()) == null) {
                    context.startActivity(intent);
                } else {
                    Toast.makeText(context, "WhatsApp is not installed on your device", Toast.LENGTH_SHORT).show();
                    sendSMS(context, phoneNumber);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void sendSMS(Context context, String phoneNumber) {
            try {
                Uri uri = Uri.parse("smsto:" + phoneNumber);
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
