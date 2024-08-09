package com.example.layersdiseasedetection;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.layersdiseasedetection.data.Message;
import com.example.layersdiseasedetection.ui.chats.FragmentChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private ImageButton buttonSend;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;

    private TextView Tvback,Tvlogout;
    private FirebaseAuth mAuth;
    private List<Message> messageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        editTextMessage = findViewById(R.id.chatEditText);
        buttonSend = findViewById(R.id.btnSendMessage);
        recyclerView = findViewById(R.id.recyclerView);
        Tvback=findViewById(R.id.TVback);
        Tvlogout=findViewById(R.id.TVLogout);

        mAuth = FirebaseAuth.getInstance();

        // Initialize the MessageAdapter with the messageList
        messageAdapter = new MessageAdapter(messageList, mAuth.getCurrentUser());

        // Set up the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        buttonSend.setOnClickListener(view -> sendMessageToConversation());

        //back
        Tvback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                FragmentChat fragmentChat = new FragmentChat();
                fragmentTransaction.replace(R.id.mobile_navigation, fragmentChat); // Assuming R.id.fragment_container is the ID of your container layout for fragments
                fragmentTransaction.addToBackStack(null); // Optional: if you want to add the transaction to the back stack
                fragmentTransaction.commit();
            }
        });
        Tvlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();

                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });


        loadConversationMessages();
    }

    private void loadConversationMessages() {
        DatabaseReference conversationsRef = FirebaseDatabase.getInstance().getReference("conversations");
        String recipientEmail= getIntent().getStringExtra("recipientEmail").replace(".", "_");;

        String currentUserEmail = mAuth.getCurrentUser().getEmail().replace(".", "_");


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
        DatabaseReference contactRef = FirebaseDatabase.getInstance().getReference("Users");

        String currentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Retrieve sender name from the database
        contactRef.child(currentUserUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String senderName = snapshot.child("name").getValue(String.class);

                            //extract data from the intents
                            Log.d("sender", "senderName: " + senderName);

                            String recipientEmail = getIntent().getStringExtra("recipientEmail").replace(".", "_");
                            String currentUserEmail = mAuth.getCurrentUser().getEmail().replace(".", "_");

                            // Ensure consistent order for conversationId
                            String conversationId = currentUserEmail.compareTo(recipientEmail) < 0 ?
                                    currentUserEmail + "_" + recipientEmail : recipientEmail + "_" + currentUserEmail;

                            DatabaseReference messagesRef = conversationsRef.child(conversationId).child("messages");

                            String message = editTextMessage.getText().toString();
                            String sender = currentUserEmail;
                            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                            Map<String, Object> messageData = new HashMap<>();
                            messageData.put("sender", sender);
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

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("ChatApp", "Failed to retrieve user details: " + error.getMessage());
                    }
                });
    }


}