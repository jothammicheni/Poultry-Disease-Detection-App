package com.example.layersdiseasedetection.ui.feedFomulation;

import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.layersdiseasedetection.R;
import com.example.layersdiseasedetection.databinding.FragmentFeedFormulationBinding;

public class Feed_FormulationFragment extends Fragment {

    private FragmentFeedFormulationBinding binding;
  //  private FeedFormulationViewModel viewModel;

    private EditText editBirdsNumber, editAgeNumber;
    private Button btnCalculateFeeds;
    private TextView textViewResult,textViewTotalFeed,textViewFeedType;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFeedFormulationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

       // viewModel = new ViewModelProvider(this).get(FeedFormulationViewModel.class);

        editBirdsNumber = binding.editBirdsNumber;
        editAgeNumber = binding.editAge;
        btnCalculateFeeds = binding.BtnCalculate;
        textViewResult = binding.textViewResult;
        textViewTotalFeed=binding.textViewTotalfeed;
        textViewFeedType=binding.textViewFeedType;

        btnCalculateFeeds.setOnClickListener(v -> formluateFeeds());

        return root;
    }

    private void formluateFeeds() {
        String textAge = editAgeNumber.getText().toString().trim();
        String textBirdsNum = editBirdsNumber.getText().toString().trim();

        if (textAge.isEmpty()) {
            editAgeNumber.setError("Fill the Field");
            editAgeNumber.requestFocus();
            return;
        }
        if (textBirdsNum.isEmpty()) {
            editBirdsNumber.setError("Fill the Field");
            editBirdsNumber.requestFocus();
            return;
        }

        int age = Integer.parseInt(textAge);
        int birds = Integer.parseInt(textBirdsNum);

        if (age > 90) {
            showErrorDialog("Error", "Number of weeks should not exceed 90 weeks. Please enter a valid number.");
            return;
        }else{

            int totalAmount=0;
            int form2=0;
            int form3=0;

            String feedType="";
            if(age<1){
                feedType="invalid number";
            }else if(age>0&&age<8){
                feedType="Starter";

            }else if(age>=9&&age<20){
                feedType="grower";
            }else{
                feedType="layer mash";
            }



            if(age==1){
                totalAmount=30;
            }
            else if(age>=2&&age<21){
                form2=(age*7)/2;
                totalAmount=form2+40;
            }else if(age>20){
                totalAmount=(20*7)/2+40;
            }

            textViewResult.setText(String.valueOf(totalAmount) + " (KGS)");
            textViewTotalFeed.setText(String.valueOf(totalAmount*birds) + " (KGS)");
            textViewFeedType.setText(feedType);


        }

        //double feedAmount = viewModel.calculateFeedAmount(age, birds);
       // textViewResult.setText(String.format("The recommended feed amount is %.2f kg.", feedAmount));
    }

    private void showErrorDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(title)
                .setMessage(message)
                .setNegativeButton("Cancel", (dialog, which) -> {
                    // Handle negative button click
                })
                .create()
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
