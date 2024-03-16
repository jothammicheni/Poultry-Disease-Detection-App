package com.example.layersdiseasedetection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.layersdiseasedetection.data.MedicationDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class DisplayResults extends AppCompatActivity {
    TextView TVpredictioResults, TVbackToPreviouspage,TVdosages,TVmedicinedescription,TVmedicationName;
    Button btnChatWithVet;
    ImageView IVmedicineImage;
    String url;

    DatabaseReference  dataRef;

    String disease;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_results);

        TVpredictioResults=findViewById(R.id.TVpredictionResults);
        TVbackToPreviouspage=findViewById(R.id.TVback);
        TVdosages=findViewById(R.id.TVdosages);
        btnChatWithVet=findViewById(R.id.btnChatWithVet);
        IVmedicineImage=findViewById(R.id.IVmedicationImage);
        TVmedicinedescription=findViewById(R.id.TVmedicationDescription);
        TVmedicationName=findViewById(R.id.TVmedicationName);
        Integer results=getIntent().getIntExtra("prediction",0);


        dataRef= FirebaseDatabase.getInstance().getReference("medications");

        if(results==0){
            disease="Coccidiosis";
            url="https://www.google.com";
        }
        if(results==1){
            disease="Healthy";
        }
        if(results==2){
            disease="Newcastle Virus disease";
           url="https://www.w3schools.com/colors/colors_picker.asp";

        }
        if(results==3){
            disease="Salmonella";
            url="https://github.com/jothammicheni/Poultry-Disease-Detection-App";

        }

        TVpredictioResults.setText(disease);
       // TVpredictioResults.setText(String.valueOf(results));
        Toast.makeText(this, "results"+results, Toast.LENGTH_SHORT).show();

        TVbackToPreviouspage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        TVdosages.setOnClickListener(V->dosages());

//function to display the details
        displayDetails();
    }


    public void dosages(){
        Intent intent= new Intent(Intent.ACTION_VIEW);
           intent.setData(Uri.parse(url));
           startActivity(intent);


    }

    public void displayDetails(){

        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    MedicationDetails medicationDetails=dataSnapshot.getValue(MedicationDetails.class);
                    if(medicationDetails!=null  && medicationDetails.getMedicationName().equals(disease)){

                        TVmedicinedescription.setText(medicationDetails.getMedicationDescription());
                        TVmedicationName.setText(medicationDetails.getMedicationName());
                        Picasso.get().load(medicationDetails.getImageUrl()).into(IVmedicineImage);

                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}