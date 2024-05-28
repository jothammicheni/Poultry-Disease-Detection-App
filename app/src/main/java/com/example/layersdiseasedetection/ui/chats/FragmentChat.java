package com.example.layersdiseasedetection.ui.chats;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.layersdiseasedetection.ChatActivity;
import com.example.layersdiseasedetection.ContactsAdapter;
import com.example.layersdiseasedetection.R;
import com.example.layersdiseasedetection.data.UserDetails;
import com.example.layersdiseasedetection.databinding.FragmentChatBinding;
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

public class FragmentChat extends Fragment {

    private FragmentChatBinding binding;
    private RecyclerView contactsRecyclerView;
    private DatabaseReference contactsRef;
    private ContactsAdapter contactsAdapter;
    private List<UserDetails> userList;
    private TextView TVDisplaycategory;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String currentUserCategory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize Firebase components
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        contactsRef = FirebaseDatabase.getInstance().getReference("userDetails");

        // Initialize views
        TVDisplaycategory = root.findViewById(R.id.TVcategoryDisplay);
        contactsRecyclerView = root.findViewById(R.id.rvContacts);
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userList = new ArrayList<>();
        contactsAdapter = new ContactsAdapter(userList);
        contactsRecyclerView.setAdapter(contactsAdapter);

        // Set up RecyclerView click listener
        contactsAdapter.setOnItemClickListener(user -> {
            Log.d("FragmentChat", "Clicked on user: " + user.getUsername());
            Intent intent = new Intent(requireContext(), ChatActivity.class);
            intent.putExtra("recipientEmail", user.getUseremail());
            startActivity(intent);
        });

        // Load user category and refresh user list
        if (currentUser != null) {
            retrieveUserCategory();
        } else {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            // Optionally, redirect to login screen
        }

        return root;
    }

    private void retrieveUserCategory() {
        DatabaseReference currentUserRef = contactsRef.child(currentUser.getUid());
        currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    currentUserCategory = snapshot.child("userCategory").getValue(String.class);
                    Log.d("FragmentChat", "Current user category: " + currentUserCategory);
                    refreshUserList();
                } else {
                    Log.d("FragmentChat", "User category not found");
                    Toast.makeText(requireContext(), "User category not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Failed to load user data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshUserList() {
        if (currentUserCategory == null || currentUserCategory.isEmpty()) {
            return;
        }

        Query userQuery = contactsRef.orderByChild("userCategory").equalTo(getOppositeCategory(currentUserCategory));
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot contactSnapshot : snapshot.getChildren()) {
                    UserDetails userInfo = contactSnapshot.getValue(UserDetails.class);
                    if (userInfo != null && !userInfo.getUserCategory().equals(currentUserCategory)) {
                        userList.add(userInfo);

                        if (userInfo.getUserCategory().equals("Farmer")) {
                            TVDisplaycategory.setText("Farmers");
                        } else {
                            TVDisplaycategory.setText("Veterinary Officers");
                        }
                    }
                }
                contactsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Failed to load user list", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getOppositeCategory(String category) {
        return category.equals("Farmer") ? "Veterinary Officer" : "Farmer";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
