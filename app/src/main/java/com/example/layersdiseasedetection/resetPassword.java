package com.example.layersdiseasedetection;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class resetPassword extends AppCompatActivity {
TextView TVsuccess;
EditText editEmail;
Button btnVerify,btnOpenGmail;

FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        btnVerify=findViewById(R.id.BtnReset);
        TVsuccess=findViewById(R.id.TVsuccess);
        editEmail=findViewById(R.id.editEmail);
        btnOpenGmail=findViewById(R.id.btnOpenVerificationmessage);

        mAuth=FirebaseAuth.getInstance();




        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=editEmail.getText().toString();
                if (email.isEmpty()||!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    editEmail.setError("Enter a valid Email");
                    editEmail.requestFocus();
                    btnOpenGmail.setVisibility(View.GONE);
                }else {
                    resetPassword(email);
                }
            }
        });

        btnOpenGmail.setOnClickListener(v -> openGmail());
    }

    //function to resetpassword
    private void resetPassword(String email){
        mAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        TVsuccess.setText("Verification email sent successfully");
                        btnOpenGmail.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                         TVsuccess.setText("Failed"+e);
                         btnOpenGmail.setVisibility(View.GONE);
                    }
                });
    }

    private void openGmail() {
        Intent intent=new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_APP_EMAIL);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


}