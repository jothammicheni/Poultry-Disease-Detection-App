package com.example.layersdiseasedetection;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.layersdiseasedetection.data.UserDetails;

import java.util.List;

public class MainContactsAdapter extends RecyclerView.Adapter<MainContactsAdapter.ViewHolder> {

    private List<UserDetails> userList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(UserDetails userDetails);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public MainContactsAdapter(List<UserDetails> userList) {
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
        holder.BtnWhatssap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.openWhatsAppChat(userDetails);
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.username);
            textEmail = itemView.findViewById(R.id.TVemail);
            textPhone = itemView.findViewById(R.id.TVnumber);
            LLUserHolders = itemView.findViewById(R.id.LLUserHolder);
            BtnWhatssap = itemView.findViewById(R.id.BtnWhatssap);

            // Ensure you are getting the correct layout parameters type
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) LLUserHolders.getLayoutParams();
            layoutParams.topMargin = 20; // margin in pixels
            layoutParams.bottomMargin = 20;
            layoutParams.leftMargin = 5;
            layoutParams.rightMargin = 5; // Add right margin if needed
            LLUserHolders.setLayoutParams(layoutParams);
        }

        public void bind(UserDetails userDetails) {
            textName.setText(userDetails.getUsername() + " (" + userDetails.getUserphone() + ")");
            textEmail.setText(userDetails.getUseremail());
            textPhone.setText(userDetails.getUserphone());
        }

        public void openWhatsAppChat(UserDetails userDetails) {
            try {
                // Format the phone number to the required format
                String formattedNumber = userDetails.getUserphone().replaceFirst("0", "+254");

                // Create the URI for WhatsApp
                String message = "Hello";
                String uri = "https://wa.me/" + formattedNumber + "?text=" + Uri.encode(message);

                // Create the intent
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(uri));

                // Set the package to ensure the intent opens WhatsApp
                intent.setPackage("com.whatsapp");

                // Verify that there is an app to receive the intent
                if (itemView.getContext().getPackageManager().resolveActivity(intent, 0) != null) {
                    itemView.getContext().startActivity(intent);
                } else {
                    Toast.makeText(itemView.getContext(), "WhatsApp not installed", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(itemView.getContext(), "Error opening WhatsApp", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
