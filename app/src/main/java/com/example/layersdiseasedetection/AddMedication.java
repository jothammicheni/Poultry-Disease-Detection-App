package com.example.layersdiseasedetection;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.layersdiseasedetection.data.MedicationDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class AddMedication extends AppCompatActivity {

    Spinner spinner;
    EditText medicineName, medicineDescription;
    ImageView imageView;
    private Uri filePath;
    Button addToDatabaseBtn, uploadImageBtn;

    DatabaseReference databaseReference;

    FirebaseStorage storage;
    StorageReference storageReference;

    private ActivityResultLauncher<String> pickImageLauncher;

    String[] diseases = {"Newcastle", "Coccidiosis", "Salmonella", "Healthy"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medication);

        spinner = findViewById(R.id.spinner);
        medicineName = findViewById(R.id.medicinename);
        medicineDescription = findViewById(R.id.editDescription);
        imageView = findViewById(R.id.IVimage);
        addToDatabaseBtn = findViewById(R.id.btnAddTodatabase);
        uploadImageBtn = findViewById(R.id.btnUploadImage);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, diseases);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("medications");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                imageView.setImageURI(uri);
                filePath = uri;
            } else {
                Toast.makeText(AddMedication.this, "No image found", Toast.LENGTH_SHORT).show();
            }
        });

        uploadImageBtn.setOnClickListener(v -> uploadImage());
        addToDatabaseBtn.setOnClickListener(v -> uploadProductDetails());
    }

    public void uploadImage() {
        pickImageLauncher.launch("image/*");
    }

    public void uploadProductDetails() {
        Log.d("AddMedication", "File URI: " + filePath.toString());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null && filePath != null) {
            String medicationId = UUID.randomUUID().toString().trim();
            String medicationName = medicineName.getText().toString().trim();
            String medicationDescription = medicineDescription.getText().toString().trim();
            String disease = spinner.getSelectedItem().toString();

            if (TextUtils.isEmpty(medicationName) || TextUtils.isEmpty(medicationDescription) || TextUtils.isEmpty(disease)) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            StorageReference imageRef = storageReference.child("itemImages/" + medicationId + ".jpg");

            imageRef.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            MedicationDetails medicationDetails = new MedicationDetails(medicationId,disease, medicationName, medicationDescription, uri.toString());
                            databaseReference.child(medicationId).setValue(medicationDetails);

                            Toast.makeText(this, "Product added successfully", Toast.LENGTH_SHORT).show();
                        });

                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("uploadError", "Upload Failed", e);
                    });
        } else {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
        }
    }
}
