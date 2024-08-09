package com.example.layersdiseasedetection;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.layersdiseasedetection.data.UserDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DisplayUsers extends AppCompatActivity {

    private RecyclerView contactsRecyclerView;
    private DatabaseReference contactsRef, usersRef;
    private ContactsAdapter contactsAdapter;

    TextView TVbackIcon, TVlogout;
    private List<UserDetails> userList;
    private TextView TVDisplaycategory;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_users);

        // Initialize views
        TVDisplaycategory = findViewById(R.id.TVcategoryDisplay);
        TVlogout = findViewById(R.id.TVLogout);
        TVbackIcon = findViewById(R.id.TVback);

        // Initialize Firebase
        initializeFirebase();

        // Setup RecyclerView
        setupRecyclerView();

        // Check if user is logged in
        if (currentUser == null) {
            refreshUserList(); // Load all users
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            // Optionally, redirect to login screen
        }
    }

    private void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        contactsRef = FirebaseDatabase.getInstance().getReference("newUserDetails");
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
    }

    private void setupRecyclerView() {
        contactsRecyclerView = findViewById(R.id.rvContacts);
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        contactsAdapter = new ContactsAdapter(userList);
        contactsRecyclerView.setAdapter(contactsAdapter);

        // Set item click listener
        contactsAdapter.setOnItemClickListener(user -> {
            Intent intent = new Intent(getApplicationContext(), NewUserDetailsConfirmation.class);
            intent.putExtra("username", user.getUsername());
            intent.putExtra("email", user.getUseremail());
            intent.putExtra("phone", user.getUserphone());
            intent.putExtra("city", user.getUserCity());
            intent.putExtra("password", user.getUserpassword());
            intent.putExtra("latitude", user.getLatitude());
            intent.putExtra("longitude", user.getLongitude());
            startActivity(intent);
        });

        // Back button listener
        TVbackIcon.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(),AdminPanel.class);
            startActivity(intent);
        });

        // Logout button listener
        TVlogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(DisplayUsers.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            // Optionally, redirect to login screen
        });
    }

    private void refreshUserList() {
        contactsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<UserDetails> tempList = new ArrayList<>();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    UserDetails userInfo = userSnapshot.getValue(UserDetails.class);
                    if (userInfo != null) {
                        tempList.add(userInfo);
                    }
                }
                checkUsersAgainstDatabase(tempList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DisplayUsers.this, "Failed to load user list", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUsersAgainstDatabase(List<UserDetails> tempList) {
        userList.clear();
        for (UserDetails userInfo : tempList) {
            usersRef.orderByChild("email").equalTo(userInfo.getUseremail()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        userList.add(userInfo);
                    }

                    // Check if all users have been processed
                    if (userList.size() == tempList.size()) {
                        updateUI();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(DisplayUsers.this, "Failed to check email in Users database", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateUI() {
        // Update UI
        if (!userList.isEmpty()) {
            TVDisplaycategory.setText("All Users");
        } else {
            TVDisplaycategory.setText("No users found");
        }
        contactsAdapter.notifyDataSetChanged();
    }

}
