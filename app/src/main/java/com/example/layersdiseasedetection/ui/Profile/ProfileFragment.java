package com.example.layersdiseasedetection.ui.Profile;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.layersdiseasedetection.R;
import com.example.layersdiseasedetection.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding=FragmentProfileBinding.inflate(inflater,container,false);
        View root=binding.getRoot();

        return root;


    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}