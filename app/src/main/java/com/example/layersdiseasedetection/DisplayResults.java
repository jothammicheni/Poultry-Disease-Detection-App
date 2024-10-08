package com.example.layersdiseasedetection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.layersdiseasedetection.data.Medication;
import com.example.layersdiseasedetection.data.MedicationDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class DisplayResults extends AppCompatActivity {

    TextView TVpredictionResults, TVmedicationName, TVmedicationDescription, TVdosages;
    Button btnChatWithVet,btnCalculateSpread;
    ImageView IVmedicationImage;

    DatabaseReference dataRef;
    Integer results;
    private TextView Tvback,Tvlogout;
    String disease;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_results);

        TVpredictionResults = findViewById(R.id.TVpredictionResults);
        TVmedicationName = findViewById(R.id.TVmedicationName);
        TVmedicationDescription = findViewById(R.id.TVmedicationDescription);
        TVdosages = findViewById(R.id.TVdosages);
        btnChatWithVet = findViewById(R.id.btnChatWithVet);
        IVmedicationImage = findViewById(R.id.IVmedicationImage);
        btnCalculateSpread=findViewById(R.id.btnCalculateSpread);
        Tvback=findViewById(R.id.TVback);
        Tvlogout=findViewById(R.id.TVLogout);

         results = getIntent().getIntExtra("prediction", 0);

        dataRef = FirebaseDatabase.getInstance().getReference("medications");

        if (results == 0) {
            disease = "Coccidiosis";
            url = "https://go.drugbank.com/drugs/DB00415";
        } else if (results == 1) {
            disease = "Healthy";
        } else if (results == 2) {
            disease = "Newcastle Virus disease";
            url = "https://vetvaco.com.vn/en/lasota-vaccine";
        } else if (results == 3) {
            disease = "Salmonella";

            url = "https://www.mayoclinic.org/diseases-conditions/salmonella/diagnosis-treatment/drc-20355335";

        }

        TVpredictionResults.setText(disease);

        TVdosages.setOnClickListener(view -> dosages());

        // Function to display the medication details
        displayDetails();

        btnChatWithVet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),DisplayUsers.class);
                startActivity(intent);
            }
        });


        Tvlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();

                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);

            }
        });
        //
        Tvback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        btnCalculateSpread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), DiseaseSpread.class);
                intent.putExtra("result",results);
                startActivity(intent);
            }
        });
    }

    public void dosages() {
        if (!TextUtils.isEmpty(url)) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } else {
            Toast.makeText(this, "No dosages available for this disease", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayDetails() {
        String drugName, drugDescription;
           int drugImage;
        Medication medication = new Medication();

        if (results == 0) {

            drugName = medication.getCoccodiocis()[0];
            drugDescription = medication.getCoccodiocis()[1];
                TVmedicationName.setText(drugName);
                TVmedicationDescription.setText(drugDescription);
                IVmedicationImage.setImageResource(R.mipmap.cocci_drug_foreground);

        }
        if (results == 2) {
            drugName = medication.getNewcastle()[0];
            drugDescription = medication.getNewcastle()[1];
            TVmedicationName.setText(drugName);
            TVmedicationDescription.setText(drugDescription);
            IVmedicationImage.setImageResource(R.drawable.newcastle);

        }
        if (results == 3) {
            drugName = medication.getSalmonella()[0];
            drugDescription = medication.getSalmonella()[1];
            TVmedicationName.setText(drugName);
            TVmedicationDescription.setText(drugDescription);
            IVmedicationImage.setImageResource(R.drawable.newcastle);

        }
    }


    }


