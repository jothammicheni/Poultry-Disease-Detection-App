package com.example.layersdiseasedetection;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.layersdiseasedetection.data.UserDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NewUserDetailsConfirmation extends AppCompatActivity {

    private EditText editUsername, editEmail, editPhone, editCity, editPassword, editLatitude, editLongitude;
    private TextView TVback, TVLogout;
    private Button btnSignUp;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user_details_confirmation);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Initialize views
        editUsername = findViewById(R.id.editUsername);
        editEmail = findViewById(R.id.editEmail);
        editPhone = findViewById(R.id.editPhone);
        editCity = findViewById(R.id.editCity);
        editPassword = findViewById(R.id.editPassword);
        editLatitude = findViewById(R.id.editLatitude);
        editLongitude = findViewById(R.id.editLongitude);
        TVback = findViewById(R.id.TVback);
        TVLogout = findViewById(R.id.TVLogout);
        btnSignUp = findViewById(R.id.btnSignUp);

        // Get the data from the intent
        String username = getIntent().getStringExtra("username");
        String email = getIntent().getStringExtra("email");
        String phone = getIntent().getStringExtra("phone");
        String city = getIntent().getStringExtra("city");
        String password = getIntent().getStringExtra("password");
        double latitude = getIntent().getDoubleExtra("latitude", 0.0); // Default to 0.0 if not found
        double longitude = getIntent().getDoubleExtra("longitude", 0.0); // Default to 0.0 if not found

        // Set the data to the EditText fields
        editUsername.setText(username);
        editEmail.setText(email);
        editPhone.setText(phone);
        editCity.setText(city);
        editPassword.setText(password);
        editLatitude.setText(String.valueOf(latitude));
        editLongitude.setText(String.valueOf(longitude));

        // Disable the EditText fields
        editUsername.setEnabled(false);
        editEmail.setEnabled(false);
        editPhone.setEnabled(false);
        editCity.setEnabled(false);
        editPassword.setEnabled(false);
        editLatitude.setEnabled(false);
        editLongitude.setEnabled(false);

        // Back button listener
        TVback.setOnClickListener(v -> finish()); // Close this activity

        // Logout button listener
        TVLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(NewUserDetailsConfirmation.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity
        });

        // Sign-up button listener
        btnSignUp.setOnClickListener(v -> loginUser(email, password, username, phone, city, latitude, longitude));
    }

    private void loginUser(String email, String password, String username, String phone, String city, double latitude, double longitude) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success
                        FirebaseUser user = mAuth.getCurrentUser();
                        addUserToDatabase(user.getUid(), username, email, password, phone, city, latitude, longitude);
                        Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("NewUserDetailsConfirmation", "signInWithEmail:failure", task.getException());
                        Toast.makeText(NewUserDetailsConfirmation.this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void addUserToDatabase(String userId, String username, String email, String password, String phone, String city, double latitude, double longitude) {
        UserDetails userDetails = new UserDetails(username, email, password, phone, "Farmer", city, latitude, longitude);
        databaseReference.child(userId).setValue(userDetails)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(NewUserDetailsConfirmation.this, "User data added to database.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(NewUserDetailsConfirmation.this, "Failed to add user data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
