package com.example.layersdiseasedetection;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import com.example.layersdiseasedetection.data.UserDetails;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class newuserRegistration extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    EditText editName, editEmail, editPassword, editPhone, editCity;
    Button btnRegister;
    ProgressBar PBprogress;
    private TextView Tvback, Tvlogout;
    RadioButton radioFarmer, radioVetOfficer;

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    GoogleMap mMap;
    LatLng userLocation; // Store user location
    LocationCallback locationCallback;
    FusedLocationProviderClient fusedLocationClient; // Initialize FusedLocationProviderClient

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newuser_registration);

        // Initialize views
        btnRegister = findViewById(R.id.btnSignUp);
        editEmail = findViewById(R.id.editEmail);
        editName = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        editPhone = findViewById(R.id.editPhone);
        editCity = findViewById(R.id.editCity);
        PBprogress = findViewById(R.id.progress);
        radioFarmer = findViewById(R.id.radioFarmer);
        radioVetOfficer = findViewById(R.id.radioVetOfficer);
        Tvback = findViewById(R.id.TVback);
        Tvlogout = findViewById(R.id.TVLogout);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("newUserDetails");

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Register button click listener
        btnRegister.setOnClickListener(v -> RegisterNewUser());

        Tvback.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AdminPanel.class);
            startActivity(intent);
        });

        Tvlogout.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        });

        // Check for location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000); // 10 seconds
        locationRequest.setFastestInterval(5000); // 5 seconds

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    updateMapWithLocation();
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Start location updates
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    private void updateMapWithLocation() {
        if (mMap != null && userLocation != null) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
        }
    }

    public void RegisterNewUser() {
        String username = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String password = editPassword.getText().toString().trim(); // Include password
        String city = editCity.getText().toString().trim();
        String userCategory = radioFarmer.isChecked() ? "Farmer" : "Veterinary Officer";

        // Validate input fields
        if (username.isEmpty()) {
            editName.setError("Enter a valid name");
            editName.requestFocus();
            return;
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Enter a valid email");
            editEmail.requestFocus();
            return;
        }
        if (phone.isEmpty()) {
            editPhone.setError("Phone number can't be empty");
            editPhone.requestFocus();
            return;
        }
        if (city.isEmpty()) {
            editCity.setError("City can't be empty");
            editCity.requestFocus();
            return;
        }
        if (password.isEmpty() || password.length() < 6) {
            editPassword.setError("Enter at least 6 characters for the password");
            editPassword.requestFocus();
            return;
        }

        PBprogress.setVisibility(View.VISIBLE);

        // Use latitude and longitude from userLocation
        double latitude = userLocation.latitude; // Get latitude
        double longitude = userLocation.longitude; // Get longitude

        // Generate a unique key for the new user details
        String userId = databaseReference.push().getKey();
        UserDetails user = new UserDetails(username, email, password, phone, userCategory, city, latitude, longitude); // Include latitude and longitude

        // Store user details in Firebase Realtime Database
        databaseReference.child(userId).setValue(user)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(newuserRegistration.this, "User details added", Toast.LENGTH_SHORT).show();
                    PBprogress.setVisibility(View.GONE);

                    Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                    startActivity(intent);
                    // Optionally, navigate to another activity or clear the form
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(newuserRegistration.this, "Failed to add user details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    PBprogress.setVisibility(View.GONE);
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
}
