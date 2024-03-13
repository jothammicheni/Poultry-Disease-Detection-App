package com.example.layersdiseasedetection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {
  TextView tvbackTologin;

  Spinner spinnerCategory, spinnerCounty;
    String[] Counties = {"Baringo", "Bomet", "Bungoma", "Busia", "Elgeyo-Marakwet", "Embu", "Garissa", "Homa Bay", "Isiolo", "Kajiado", "Kakamega", "Kericho", "Kiambu", "Kilifi", "Kirinyaga", "Kisii", "Kisumu", "Kitui", "Kwale", "Laikipia", "Lamu", "Machakos", "Makueni", "Mandera", "Marsabit", "Meru", "Migori", "Mombasa", "Murang'a", "Nairobi", "Nakuru", "Nandi", "Narok", "Nyamira", "Nyandarua", "Nyeri", "Samburu", "Siaya", "Taita-Taveta", "Tana River", "Tharaka-Nithi", "Trans-Nzoia", "Turkana", "Uasin Gishu", "Vihiga", "Wajir", "West Pokot"};
    String[] userCategories={"Farmer","Veterinary Officer"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tvbackTologin=findViewById(R.id.TVbackTologin);
        spinnerCategory=findViewById(R.id.spinnerCategory);
        spinnerCounty=findViewById(R.id.spinnerCounty);

      // set the counties in the sppiner
        ArrayAdapter<CharSequence> adapter=new ArrayAdapter<>(this , android.R.layout.simple_spinner_item,Counties);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCounty.setAdapter(adapter);
        String selection = "Choose yor residence county";
        int spinnerPosition = adapter.getPosition(selection);
        spinnerCounty.setSelection(spinnerPosition);


        // Set the user categories in a separate spinner
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, userCategories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
        String categoryselection = "Farmer";
        int CategoryspinnerPosition =categoryAdapter.getPosition(categoryselection);
        spinnerCounty.setSelection(CategoryspinnerPosition);










        tvbackTologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}