package com.example.layersdiseasedetection;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.layersdiseasedetection.data.Message;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private static List<Message> messageList;
    private static FirebaseUser currentUser;

    public MessageAdapter(List<Message> messageList, FirebaseUser currentUser) {
        this.messageList = messageList;
        this.currentUser = currentUser;
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewMessage, textName, textTimestamp;
        private LinearLayout chatHolderLayout, messageHolderLayout;
        private ImageButton btndeleteChat;
        private MessageAdapter messageAdapter;

        public MessageViewHolder(@NonNull View itemView, MessageAdapter messageAdapter) {
            super(itemView);
            this.messageAdapter = messageAdapter;
            textViewMessage = itemView.findViewById(R.id.message);
            textName = itemView.findViewById(R.id.name);
            textTimestamp = itemView.findViewById(R.id.timestamp);
            chatHolderLayout = itemView.findViewById(R.id.LLChatHolder);
            messageHolderLayout = itemView.findViewById(R.id.LLMessageHolder);
            btndeleteChat = itemView.findViewById(R.id.btnDelChat);

            chatHolderLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    Message message = messageList.get(position);
                    message.setSelected(!message.isSelected());
                    chatHolderLayout.setBackgroundColor(message.isSelected() ?
                            itemView.getResources().getColor(android.R.color.holo_red_dark) :
                            itemView.getResources().getColor(android.R.color.transparent));
                    messageHolderLayout.setBackgroundColor(itemView.getResources().getColor(android.R.color.holo_red_dark));
                    return false;
                }
            });

            btndeleteChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    messageAdapter.deleteSelectedMessages();
                }
            });
        }

        public void bind(Message message) {
            textViewMessage.setText(message.getMessage());
            textTimestamp.setText(message.getTimestamp());

            if (currentUser != null && currentUser.getEmail().replace(".", "_").equals(message.getSender())) {
                textName.setText("You");
                messageHolderLayout.setBackgroundResource(R.drawable.messagebackground1);
                chatHolderLayout.setGravity(Gravity.END);
            } else {
                textName.setText(message.getRecipientname());
                messageHolderLayout.setBackgroundResource(R.drawable.messagebackground2);
                chatHolderLayout.setGravity(Gravity.START);
            }

            chatHolderLayout.setBackgroundColor(message.isSelected() ?
                    itemView.getResources().getColor(android.R.color.holo_red_dark) :
                    itemView.getResources().getColor(android.R.color.transparent));
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.bind(message);
        Log.d("ChatApp", "Binding Message: " + message);
    }

    public void deleteSelectedMessages() {
        for (int i = messageList.size() - 1; i >= 0; i--) {
            if (messageList.get(i).isSelected()) {
                messageList.remove(i);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
