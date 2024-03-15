package com.example.layersdiseasedetection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.layersdiseasedetection.data.UserDetails;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
  TextView tvbackTologin;
  EditText editName,editEmail,editPassword,editPhone;
  Button btnRegister;
    ProgressBar PBprogress;
  Spinner spinnerCategory, spinnerCounty;
    String[] Counties = {"Baringo", "Bomet", "Bungoma", "Busia", "Elgeyo-Marakwet", "Embu", "Garissa", "Homa Bay", "Isiolo", "Kajiado", "Kakamega", "Kericho", "Kiambu", "Kilifi", "Kirinyaga", "Kisii", "Kisumu", "Kitui", "Kwale", "Laikipia", "Lamu", "Machakos", "Makueni", "Mandera", "Marsabit", "Meru", "Migori", "Mombasa", "Murang'a", "Nairobi", "Nakuru", "Nandi", "Narok", "Nyamira", "Nyandarua", "Nyeri", "Samburu", "Siaya", "Taita-Taveta", "Tana River", "Tharaka-Nithi", "Trans-Nzoia", "Turkana", "Uasin Gishu", "Vihiga", "Wajir", "West Pokot"};
    String[] userCategories={"Farmer","Veterinary Officer"};

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //define the views
        btnRegister=findViewById(R.id.btnSignUp);
        tvbackTologin=findViewById(R.id.TVbackTologin);
        spinnerCategory=findViewById(R.id.spinnerCategory);
        spinnerCounty=findViewById(R.id.spinnerCounty);
        editEmail=findViewById(R.id.editEmail);
        editName=findViewById(R.id.editUsername);
        editPassword=findViewById(R.id.editPassword);
        editPhone=findViewById(R.id.editPhone);
        PBprogress=findViewById(R.id.progress);

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


      //Initialize the database
        mAuth=FirebaseAuth.getInstance();
        databaseReference=FirebaseDatabase.getInstance().getReference("userDetails");







        tvbackTologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(v->RegisterNewUser());
    }



    public void RegisterNewUser(){

        String username=editName.getText().toString().trim();
        String email=editEmail.getText().toString().trim();
        String phone=editPhone.getText().toString().trim();
        String password=editPassword.getText().toString().trim();
        
        // validate a phone number
        String phoneNumber = "0712345678"; // Example phone number

        // Regular expression for 10-digit phone number starting with 07 or 01
        String regex = "(07|01)\\d{8}";

        // Compile the regex pattern
        Pattern pattern = Pattern.compile(regex);

        // Match the phone number against the pattern
        Matcher matcher = pattern.matcher(phoneNumber);


        if(username.isEmpty()){
            editName.setError("Enter a valid name");
            editName.requestFocus();
            return;
        }
        if(email.isEmpty()||!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editEmail.setError("Enter a valid email");
            editEmail.requestFocus();
            return;
        }
       if(phone.isEmpty()){
            editPhone.setError("phone number can't be empty");
            editPhone.requestFocus();

        } if (phone.length()<10 || phone.length()>10) {
            editPhone.setError("phone number must be 10  digits");
            editPhone.requestFocus();
              return;
        }  if (!matcher.matches()) {
            editPhone.setError("wrong phone number format");
            editPhone.requestFocus();

        }

        if(password.isEmpty()|| password.length()<6){
            editPassword.setError("Enter atleast 6 characters");
            editPassword.requestFocus();
            return;

        }

        // send user to datebase
        PBprogress.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        String userid=mAuth.getCurrentUser().getUid();
                        DatabaseReference useRef=databaseReference.child(userid);
                        UserDetails user=new UserDetails(username,email,phone,password);
                        useRef.setValue(user)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(RegisterActivity.this, "User registered", Toast.LENGTH_SHORT).show();
                                       Intent intent =new Intent(getApplicationContext(),LoginActivity.class);
                                       startActivity(intent);

                                        PBprogress.setVisibility(View.GONE);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(RegisterActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();

                                    }
                                });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, "" +e, Toast.LENGTH_SHORT).show();
                        PBprogress.setVisibility(View.GONE);
                        btnRegister.setText("Try Again");
                    }
                });


    }
}