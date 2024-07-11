package com.example.layersdiseasedetection.ui.feedFomulation;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.layersdiseasedetection.R;
import com.example.layersdiseasedetection.databinding.FragmentFeedFormulationBinding;

public class Feed_FormulationFragment extends Fragment {

    private FragmentFeedFormulationBinding binding;

    private EditText editBirdsNumber, editAgeNumber;
    private LinearLayout LLresults;
    private Button btnCalculateFeeds;

    CardView cardViewCost;
    private TextView textViewResult, textViewTotalFeed, textViewFeedType, textViewCost;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFeedFormulationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        editBirdsNumber = binding.editBirdsNumber;
        editAgeNumber = binding.editAge;
        btnCalculateFeeds = binding.BtnCalculate;
        textViewResult = binding.textViewResult;
        textViewTotalFeed = binding.textViewTotalfeed;
        textViewFeedType = binding.textViewFeedType;
        LLresults = binding.LLresults;
        textViewCost = binding.textViewCost;
        cardViewCost=binding.cardviewcost;

        cardViewCost.setVisibility(View.GONE);
        LLresults.setVisibility(View.GONE);

        btnCalculateFeeds.setOnClickListener(v -> formulateFeeds());

        return root;
    }

    private void formulateFeeds() {
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
        }

        int totalAmount = 0;
        int form2 = 0;
        String feedType = "";

        if (age < 1) {
            feedType = "invalid number";
        } else if (age > 0 && age < 8) {
            feedType = "Starter chick mash";
        } else if (age >= 9 && age < 20) {
            feedType = "grower mash";
        } else {
            feedType = "layer mash";
        }

        if (age == 1) {
            totalAmount = 30;
        } else if (age >= 2 && age < 21) {
            form2 = (age * 7) / 2;
            totalAmount = form2 + 40;
        } else if (age > 20) {
            totalAmount = (20 * 7) / 2 + 40;
        }

        LLresults.setVisibility(View.VISIBLE);
        int finalAmount=totalAmount;
        textViewResult.setText(String.valueOf(totalAmount));
        textViewTotalFeed.setText(String.valueOf((finalAmount * birds) / 1000));
        textViewFeedType.setText(feedType);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Enter cost of " + feedType + " in your region");

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.edittextbackground));
        // Set the top margin
        ViewGroup.LayoutParams layoutParams = input.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ((ViewGroup.MarginLayoutParams) layoutParams).topMargin = 20;
            input.setLayoutParams(layoutParams);
        }
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String value = input.getText().toString();
            if (value.isEmpty()) {
                Toast.makeText(getContext(), "Cost cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double cost = Double.parseDouble(value);
                int total = (int) (finalAmount * birds) / 1000;
                textViewCost.setText(String.valueOf("Ksh :"+ cost * total));
                //textViewCost.setText(String.valueOf(cost*total));
                LLresults.setVisibility(View.VISIBLE);
               cardViewCost.setVisibility(View.VISIBLE);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Invalid cost input", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
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
