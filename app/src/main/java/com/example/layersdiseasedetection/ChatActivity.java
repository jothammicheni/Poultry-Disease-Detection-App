package com.example.layersdiseasedetection;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.layersdiseasedetection.data.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private EditText editTextMessage;
    private Button buttonSend;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private FirebaseAuth mAuth;
    private List<Message> messageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.btnSendMessage);
        recyclerView = findViewById(R.id.recyclerView);

        mAuth = FirebaseAuth.getInstance();

        // Initialize the MessageAdapter with the messageList
        messageAdapter = new MessageAdapter(messageList, mAuth.getCurrentUser());

        // Set up the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        buttonSend.setOnClickListener(view -> sendMessageToConversation());

        loadConversationMessages();
    }



    private void loadConversationMessages() {
        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Retrieve the current user's category
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("userDetails").child(currentUserUid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String currentUserCategory = snapshot.child("userCategory").getValue(String.class);

                    // Determine the recipient's category based on the current user's category
                    String recipientCategory = currentUserCategory.equals("FARMER") ? "VET OFFICER" : "FARMER";

                    // Query the user details to find a user with the recipient category
                    DatabaseReference recipientRef = FirebaseDatabase.getInstance().getReference("userDetails");
                    recipientRef.orderByChild("userCategory").equalTo(recipientCategory)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                        // Assuming there's only one user with the recipient category, you can directly get the email
                                        String recipientEmail = userSnapshot.child("useremail").getValue(String.class);
                                        // Now, use the recipientEmail to load conversation messages
                                        loadMessagesForConversation(recipientEmail);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.e("ChatApp", "Failed to retrieve recipient: " + databaseError.getMessage());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ChatApp", "Failed to retrieve user category: " + error.getMessage());
            }
        });
    }

    private void loadMessagesForConversation(String recipientEmail) {
        DatabaseReference conversationsRef = FirebaseDatabase.getInstance().getReference("conversations");

        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", "_");

        // Ensure consistent order for conversationId
        String conversationId = currentUserEmail.compareTo(recipientEmail) < 0 ?
                currentUserEmail + "_" + recipientEmail : recipientEmail + "_" + currentUserEmail;

        conversationsRef.child(conversationId).child("messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        messageList.clear(); // Clear existing messages
                        for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                            Message message = messageSnapshot.getValue(Message.class);
                            if (message != null) {
                                messageList.add(message);
                            }
                        }
                        messageAdapter.notifyDataSetChanged(); // Notify adapter of data change
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("ChatApp", "Failed to retrieve messages: " + databaseError.getMessage());
                    }
                });
    }

    private void sendMessageToConversation() {
        DatabaseReference conversationsRef = FirebaseDatabase.getInstance().getReference("conversations");
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("userDetails");

        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Retrieve sender name and category from the database
        userRef.child(currentUserUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String senderName = snapshot.child("username").getValue(String.class);
                    String senderCategory = snapshot.child("userCategory").getValue(String.class);

                    // Determine the recipient's category based on the sender's category
                    String recipientCategory = senderCategory.equals("FARMER") ? "VET OFFICER" : "FARMER";

                    // Query the user details to find a user with the recipient category
                    userRef.orderByChild("userCategory").equalTo(recipientCategory)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                        // Assuming there's only one user with the recipient category, you can directly get the email
                                        String recipientEmail = userSnapshot.child("useremail").getValue(String.class);
                                        // Now, send message to the conversation between sender and recipient
                                        sendMessageToConversation(senderName, recipientEmail);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.e("ChatApp", "Failed to retrieve recipient: " + databaseError.getMessage());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ChatApp", "Failed to retrieve sender details: " + error.getMessage());
            }
        });
    }

    private void sendMessageToConversation(String senderName, String recipientEmail) {
        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".", "_");

        // Ensure consistent order for conversationId
        String conversationId = currentUserEmail.compareTo(recipientEmail) < 0 ?
                currentUserEmail + "_" + recipientEmail : recipientEmail + "_" + currentUserEmail;

        DatabaseReference messagesRef = FirebaseDatabase.getInstance().getReference("conversations").child(conversationId).child("messages");

        String message = editTextMessage.getText().toString();
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        Map<String, Object> messageData = new HashMap<>();
        messageData.put("sender", currentUserEmail);
        messageData.put("recipientName", senderName); // Use senderName instead of getIntent().getStringExtra("name")
        messageData.put("message", message);
        messageData.put("timestamp", timestamp);

        messagesRef.push().setValue(messageData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("ChatApp", "Message sent successfully");
                    // Clear the message input field after sending
                    editTextMessage.setText("");
                })
                .addOnFailureListener(e -> Log.e("ChatApp", "Error sending message: " + e.getMessage()));
    }


}
