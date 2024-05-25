package com.example.layersdiseasedetection;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.layersdiseasedetection.data.Message;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    static class MessageViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewMessage, textName, textTimestamp;
        private LinearLayout chatHolderLayout, messageHolderLayout;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.message);
            textName = itemView.findViewById(R.id.name);
            textTimestamp = itemView.findViewById(R.id.timestamp);
            chatHolderLayout=itemView.findViewById(R.id.LLChatHolder);
            messageHolderLayout=itemView.findViewById(R.id.LLMessageHolder);
        }

        public void bind(Message message) {



            textViewMessage.setText(message.getMessage());
            textTimestamp.setText(message.getTimestamp());

            // Show/hide sender name based on whether the message is from the current user
            if (currentUser != null && currentUser.getEmail().replace(".","_").equals(message.getSender())) {
                textName.setText("You");
                messageHolderLayout.setBackgroundResource(R.drawable.messagebackground1);
                chatHolderLayout.setGravity(Gravity.END);


            } else {
                textName.setText(message.getRecipientname());
                messageHolderLayout.setBackgroundResource(R.drawable.messagebackground2);
                chatHolderLayout.setGravity(Gravity.START);
            }
        }
    }

    private List<Message> messageList;

    private static FirebaseUser currentUser;

    public MessageAdapter(List<Message> messageList, FirebaseUser currentUser) {
        this.messageList = messageList;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);

        holder.bind(message);

        Log.d("ChatApp", "Binding Message: " + message);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
