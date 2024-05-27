package com.example.layersdiseasedetection;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.layersdiseasedetection.data.UserDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DisplayUsers extends AppCompatActivity {

    private RecyclerView contactsRecyclerView;
    private DatabaseReference contactsRef;
    private ContactsAdapter contactsAdapter;
    private List<UserDetails> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_users);

        contactsRecyclerView = findViewById(R.id.rvContacts);
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        userList = new ArrayList<>();
        contactsAdapter = new ContactsAdapter(userList);
        contactsRecyclerView.setAdapter(contactsAdapter);

        contactsRef = FirebaseDatabase.getInstance().getReference("userDetails");

        contactsAdapter.setOnItemClickListener(user -> {
            Log.d("ChatApp", "Clicked on user: " + user.getUsername());
            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
            intent.putExtra("recipientEmail", user.getUseremail());
            startActivity(intent);
        });

        contactsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot contactSnapshot : snapshot.getChildren()) {
                    String name = contactSnapshot.child("username").getValue(String.class);
                    String email = contactSnapshot.child("useremail").getValue(String.class);
                    String  phone = contactSnapshot.child("userphone").getValue(String.class);

                    UserDetails userInfo = new UserDetails(name, email, "", phone,"");
                    userList.add(userInfo);
                }
                contactsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DisplayUsers.this, "Failed to load user list", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
