package com.example.layersdiseasedetection.ui.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.layersdiseasedetection.DisplayResults;
import com.example.layersdiseasedetection.databinding.FragmentHomeBinding;
import com.example.layersdiseasedetection.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
   ImageView  IVimage;
   TextView TVresults;
    private Bitmap img;

    private Model model;
   Button btnUploadImage, btnPredict;

    private static final float CONFIDENCE_THRESHOLD = (float) 0.95;
    private ActivityResultLauncher<String> pickImageLauncher;
    public View onCreateView(@NonNull LayoutInflater inflater,
    ViewGroup container, Bundle savedInstanceState) {
   // HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
      binding = FragmentHomeBinding.inflate(inflater, container, false);
      View root = binding.getRoot();

        IVimage=binding.IVimage;
    btnPredict=binding.btnPredict;
    btnUploadImage=binding.btnUploadImage;
    TVresults=binding.TVresults;


        // Initialize TensorFlow Lite model
        try {
            model = Model.newInstance(requireContext());
        } catch (IOException e) {
            Toast.makeText(requireContext(), "Error initializing model", Toast.LENGTH_SHORT).show();
        }

        // setup the image picker
        // Setup the image picker
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                IVimage.setImageURI(uri);
                btnUploadImage.setVisibility(View.GONE);
                btnPredict.setVisibility(View.VISIBLE);

                try {
                    img = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);

                } catch (IOException e) {
                    e.printStackTrace();
                  //  Toast.makeText(MainActivity.this, "Error loading image", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Toast.makeText(MainActivity.this, "No image found", Toast.LENGTH_SHORT).show();
            }
        });



        btnUploadImage.setOnClickListener(V->uploadImage());

        btnPredict.setOnClickListener(V->predictResults());
        IVimage.setOnClickListener(V->uploadImage());

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public  void uploadImage(){
        pickImageLauncher.launch("image/*");
    }




    public  void  predictResults(){
        if (img != null) {
            img = Bitmap.createScaledBitmap(img, 128, 128, true);

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 128, 128, 3}, DataType.FLOAT32);

            TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
            tensorImage.load(img);
            ByteBuffer byteBuffer = tensorImage.getBuffer();
            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            // Find the class with the highest probability
            int predictedClass = findMaxProbabilityClass(outputFeature0.getFloatArray());
            float confidence = outputFeature0.getFloatArray()[predictedClass];

            if (confidence >= CONFIDENCE_THRESHOLD) {
                // The prediction is confident enough; display the result
                TVresults.setText("Predicted Class: " + predictedClass);
                Toast.makeText(requireContext(), ""+confidence, Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(requireContext(), DisplayResults.class);
                intent.putExtra("prediction", predictedClass);
                startActivity(intent);

            } else {
                // The prediction is not confident; reject the result
                TVresults.setText("Image rejected- These implies that wrong or unclear image was detected or none of the diseases was detected");
                btnUploadImage.setVisibility(View.VISIBLE);
                btnPredict.setVisibility(View.GONE);
                Toast.makeText(requireContext(), ""+confidence, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private int findMaxProbabilityClass(float[] probabilities) {
        int maxClass = -1;
        float maxProbability = 0.0f;

        for (int i = 0; i < probabilities.length; i++) {
            if (probabilities[i] > maxProbability) {
                maxProbability = probabilities[i];
                maxClass = i;
            }
        }

        return maxClass;
    }


}