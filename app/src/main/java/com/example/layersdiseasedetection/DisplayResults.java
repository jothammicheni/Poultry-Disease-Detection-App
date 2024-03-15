package com.example.layersdiseasedetection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DisplayResults extends AppCompatActivity {
    TextView TVpredictioResults, TVbackToPreviouspage,TVdosages,TVmedicinedescription;
    Button btnChatWithVet;
    ImageView IVmedicineImage;
    String url;

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

        Integer results=getIntent().getIntExtra("prediction",0);


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
    }


    public void dosages(){
        Intent intent= new Intent(Intent.ACTION_VIEW);
           intent.setData(Uri.parse(url));
           startActivity(intent);


    }
}