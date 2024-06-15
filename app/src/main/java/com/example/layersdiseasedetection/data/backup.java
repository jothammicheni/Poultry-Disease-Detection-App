//package com.example.layersdiseasedetection.ui.home;
//
//import android.Manifest;
//import android.app.Activity;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.Bitmap;
//import android.net.Uri;
//import android.os.Bundle;
//import android.provider.MediaStore;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.NonNull;
//import androidx.cardview.widget.CardView;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//import androidx.fragment.app.Fragment;
//
//import com.example.layersdiseasedetection.DisplayResults;
//import com.example.layersdiseasedetection.R;
//import com.example.layersdiseasedetection.databinding.FragmentHomeBinding;
//import com.example.layersdiseasedetection.ml.Model;
//
//import org.tensorflow.lite.DataType;
//import org.tensorflow.lite.support.image.TensorImage;
//import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
//
//import java.io.IOException;
//import java.nio.ByteBuffer;
//
//public class HomeFragment extends Fragment {
//
//    private FragmentHomeBinding binding;
//    private ImageView IVimage;
//    private ImageButton btncamera;
//    private TextView TVresults,TVprocessing,TVdisease;
//    private Button btnMoreDetails;
//
//    private CardView cardView;
//
//    private LinearLayout LLDisplayButton;
//    private Bitmap img;
//    private Model model;
//    private Button btnUploadImage, btnPredict;
//
//    private static final float CONFIDENCE_THRESHOLD = 0.95f;
//    private ActivityResultLauncher<String> pickImageLauncher;
//    private ActivityResultLauncher<Intent> takePictureLauncher;
//
//    Integer results;
//    String disease;
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        binding = FragmentHomeBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();
//
//        IVimage = binding.IVimage;
//        btnPredict = binding.btnPredict;
//        btnUploadImage = binding.btnUploadImage;
//        TVresults = binding.TVresults;
//        btncamera = binding.IBopenCamera;
//        LLDisplayButton=binding.LLCamera;
//        btnMoreDetails=binding.btnMoveToResults;
//        TVdisease=binding.TVDisease;
//        TVprocessing=binding.TVprocessing;
//        cardView=binding.CardView;
//        // Initialize TensorFlow Lite model
//        try {
//            model = Model.newInstance(requireContext());
//        } catch (IOException e) {
//            Toast.makeText(requireContext(), "Error initializing model", Toast.LENGTH_SHORT).show();
//        }
//
//        // Setup the image picker
//        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
//            if (uri != null) {
//                IVimage.setImageURI(uri);
//                btnUploadImage.setVisibility(View.GONE);
//                btnPredict.setVisibility(View.VISIBLE);
//                LLDisplayButton.setVisibility(View.GONE);
//                TVresults.setText("");
//
//                try {
//                    img = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                Toast.makeText(requireContext(), "No image found", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        // Setup the camera launcher
//        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
//            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
//                Bundle extras = result.getData().getExtras();
//                img = (Bitmap) extras.get("data");
//                if (img != null) {
//                    IVimage.setImageBitmap(img);
//                    btnUploadImage.setVisibility(View.GONE);
//                    btnPredict.setVisibility(View.VISIBLE);
//                    LLDisplayButton.setVisibility(View.GONE);
//                    TVresults.setText("");
//                } else {
//                    Toast.makeText(requireContext(), "Error capturing image", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        btncamera.setOnClickListener(v -> {
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            takePictureLauncher.launch(intent);
//            TVresults.setText("");
//        });
//
//        btnUploadImage.setOnClickListener(v -> uploadImage());
//
//        btnPredict.setOnClickListener(v -> predictResults());
//
//        IVimage.setOnClickListener(v -> uploadImage());
//        btnMoreDetails.setOnClickListener(v -> movetoResults());
//
//        getPermission();
//
//        return root;
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null;
//    }
//
//    public void uploadImage() {
//        pickImageLauncher.launch("image/*");
//        TVresults.setText("");
//    }
//
//    public void  movetoResults(){
//        Intent intent = new Intent(requireContext(), DisplayResults.class);
//        intent.putExtra("prediction", results);
//        startActivity(intent);
//    }
//
//    public void predictResults() {
//        if (img != null) {
//            img = Bitmap.createScaledBitmap(img, 128, 128, true);
//
//            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 128, 128, 3}, DataType.FLOAT32);
//
//            TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
//            tensorImage.load(img);
//            ByteBuffer byteBuffer = tensorImage.getBuffer();
//            inputFeature0.loadBuffer(byteBuffer);
//
//            Model.Outputs outputs = model.process(inputFeature0);
//            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
//
//            int predictedClass = findMaxProbabilityClass(outputFeature0.getFloatArray());
//            float confidence = outputFeature0.getFloatArray()[predictedClass];
//
//            if (confidence >= CONFIDENCE_THRESHOLD && predictedClass != 1) {
//                TVresults.setText("Predicted Class: " + predictedClass);
//                // Toast.makeText(requireContext(), "Confidence: " + confidence, Toast.LENGTH_SHORT).show();
////                Intent intent = new Intent(requireContext(), DisplayResults.class);
////                intent.putExtra("prediction", predictedClass);
////                startActivity(intent);
//
//                results=predictedClass;
//                if (results == 0) {
//                    disease = "Coccidiosis";
//
//                } else if (results == 1) {
//                    disease = "Healthy";
//                } else if (results == 2) {
//                    disease = "Newcastle Virus disease";
//
//                } else if (results == 3) {
//                    disease = "Salmonella";
//                }
//                TVdisease.setText(disease);
//                cardView.setVisibility(View.VISIBLE);
//                btnPredict.setVisibility(View.GONE);
//
//
//            } else {
//                TVresults.setVisibility(View.VISIBLE);
//                TVresults.setText("No Disease detected..Please Try again ");
//                btnUploadImage.setVisibility(View.VISIBLE);
//                btnPredict.setVisibility(View.GONE);
//                LLDisplayButton.setVisibility(View.VISIBLE);
//                cardView.setVisibility(View.GONE);
//                IVimage.setImageBitmap(null);
//            }
//        } else {
//            Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private int findMaxProbabilityClass(float[] probabilities) {
//        int maxClass = -1;
//        float maxProbability = 0.0f;
//
//        for (int i = 0; i < probabilities.length; i++) {
//            if (probabilities[i] > maxProbability) {
//                maxProbability = probabilities[i];
//                maxClass = i;
//            }
//        }
//
//        return maxClass;
//    }
//
//    void getPermission() {
//        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, 11);
//        }
//    }
//
//
//
//    public void displayResults(){
//
//    }
//}
