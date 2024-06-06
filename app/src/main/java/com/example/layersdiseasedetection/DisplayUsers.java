package com.example.layersdiseasedetection;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DisplayUsers extends AppCompatActivity {

    private RecyclerView contactsRecyclerView;
    private DatabaseReference contactsRef;
    private ContactsAdapter contactsAdapter;

    TextView TVbackIcon,TVlogout;
    private List<UserDetails> userList;
    private TextView TVDisplaycategory;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String currentUserCategory;
    private String currentUserCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_users);
        TVDisplaycategory = findViewById(R.id.TVcategoryDisplay);
        TVlogout=findViewById(R.id.TVLogout);
        TVbackIcon=findViewById(R.id.TVback);
        initializeFirebase();
        setupRecyclerView();

        if (currentUser != null) {
            retrieveUserCategory();
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            // Optionally, redirect to login screen
        }
    }

    private void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        contactsRef = FirebaseDatabase.getInstance().getReference("userDetails");
    }

    private void setupRecyclerView() {
        contactsRecyclerView = findViewById(R.id.rvContacts);
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        contactsAdapter = new ContactsAdapter(userList);
        contactsRecyclerView.setAdapter(contactsAdapter);

        contactsAdapter.setOnItemClickListener(user -> {
            Log.d("DisplayUsers", "Clicked on user: " + user.getUsername());
            Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
            intent.putExtra("recipientEmail", user.getUseremail());
            startActivity(intent);
        });




        TVbackIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });
        TVlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                // Sign out the user
                FirebaseAuth.getInstance().signOut();
            }
        });
    }

    private void retrieveUserCategory() {
        DatabaseReference currentUserRef = contactsRef.child(currentUser.getUid());
        currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    currentUserCategory = snapshot.child("userCategory").getValue(String.class);
                    currentUserCity = snapshot.child("userCity").getValue(String.class);
                    Log.d("DisplayUsers", "Current city: " + currentUserCity);
                    Log.d("city", "Current user city: " + currentUserCity);

                    refreshUserList();
                } else {
                    Log.d("DisplayUsers", "User category not found");

                    Toast.makeText(DisplayUsers.this, "User category not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DisplayUsers.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshUserList() {
        if (currentUserCategory == null || currentUserCategory.isEmpty() || currentUserCity == null || currentUserCity.isEmpty()) {
            return;
        }

        Query userQuery = contactsRef.orderByChild("userCategory").equalTo("Veterinary Officer");
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot contactSnapshot : snapshot.getChildren()) {
                    UserDetails userInfo = contactSnapshot.getValue(UserDetails.class);
                    if (userInfo != null && userInfo.getUserCity().equals(currentUserCity)) {
                        userList.add(userInfo);
                    }
                }

                if (!userList.isEmpty()) {
                    TVDisplaycategory.setText("Veterinary Officers in " + currentUserCity);
                } else {
                    TVDisplaycategory.setText("No Veterinary Officers found in " + currentUserCity);
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
