package com.example.layersdiseasedetection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    TextView tvbackToRegister;
    EditText editEmail,editPassword;
    Button btnLoginAsFarmer,btnLoginAsVet;
    ProgressBar PBprogress;

    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLoginAsVet=findViewById(R.id.btnSignInAsVet);
        btnLoginAsFarmer=findViewById(R.id.btnSignInAsfarmer);
        editEmail=findViewById(R.id.editEmail);
        editPassword=findViewById(R.id.editPassword);
        PBprogress=findViewById(R.id.progress);




        mAuth=FirebaseAuth.getInstance();
        btnLoginAsVet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AdminPanel.class);
                startActivity(intent);
              // loginVet();
            }
        });

        btnLoginAsFarmer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               loginUser();

//                Intent intent=new Intent(getApplicationContext(),ChatActivity.class);
//                startActivity(intent);
            }
        });
    }

    public void loginVet() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Enter a valid email");
            editEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editPassword.setError("Password can't be empty");
            editPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editPassword.setError("Password should be at least 6 characters");
            editPassword.requestFocus();
            return;
        }

        if (email.equals("jothammurimi2001@gmail.com") && password.equals("123456")) {
            Intent intent = new Intent(getApplicationContext(), AdminPanel.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Admin does not exist!", Toast.LENGTH_SHORT).show();
        }
    }

    public  void loginUser(){
        String email=editEmail.getText().toString().trim();
        String  password=editPassword.getText().toString().trim();

        if(email.isEmpty()||!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editEmail.setError("Enter a valid email");
            editEmail.requestFocus();
            return;
        }
        if(password.isEmpty()){
            editPassword.setError("password can't be empty");
            editPassword.requestFocus();
            return;
        }if(password.length()<6){
            editPassword.setError("password should be atleast 6 characters");
            editPassword.requestFocus();
        }

        PBprogress.setVisibility(View.VISIBLE);



        mAuth.signInWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Intent intent=new Intent(getApplicationContext(),DisplayUsers.class);
                        startActivity(intent);
                        PBprogress.setVisibility(View.GONE);
                        // Toast.makeText(LoginActivity.this, "User registered", Toast.LENGTH_SHORT).show();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, ""+e, Toast.LENGTH_SHORT).show();
                        PBprogress.setVisibility(View.GONE);

                    }
                });
    }
}