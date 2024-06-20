package com.example.layersdiseasedetection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;


public class FlashPage extends AppCompatActivity {
    Button btnToLoginActivity;
    public  static  final  int FLASH_DURATION_TIMEOUT=1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_page);

        btnToLoginActivity=findViewById(R.id.btnNavigateToLogin);




        // Load the GIF from the drawable folder using Glide
        Glide.with(this)
                .asGif()
                .load(R.drawable.splash)
                .into((ImageView) findViewById(R.id.IVanimate));


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start the login activity
                Intent loginIntent = new Intent(getApplicationContext(),DiseaseSpread.class);
                startActivity(loginIntent);

                // Close this activity
                finish();
            }
        }, FLASH_DURATION_TIMEOUT);
//        btnToLoginActivity.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent= new Intent(getApplicationContext(), LoginActivity.class);
//                startActivity(intent);
//            }
//        });
    }

}