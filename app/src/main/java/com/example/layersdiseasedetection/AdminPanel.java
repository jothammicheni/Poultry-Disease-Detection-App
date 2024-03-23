package com.example.layersdiseasedetection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AdminPanel extends AppCompatActivity {
    Button btnaddnewfarmer,btnaddnewvet,btnupdateusers;
    TextView TVback,TVlogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);
        btnaddnewvet=findViewById(R.id.btnRegisterVet);
        btnaddnewfarmer=findViewById(R.id.btnRegisterFarmer);
        btnupdateusers=findViewById(R.id.btnUpdateusers);
        TVback=findViewById(R.id.TVback);
        TVlogout=findViewById(R.id.TVLogout);





        // onclicklisteners
        TVlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        //
        TVback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        btnaddnewfarmer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnaddnewvet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), RegisterVet.class);
                startActivity(intent);
            }
        });

    }





}