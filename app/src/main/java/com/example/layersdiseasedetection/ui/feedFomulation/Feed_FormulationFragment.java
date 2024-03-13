package com.example.layersdiseasedetection.ui.feedFomulation;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.layersdiseasedetection.R;
import com.example.layersdiseasedetection.databinding.FragmentFeedFormulationBinding;

public class Feed_FormulationFragment extends Fragment {

    private FragmentFeedFormulationBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
      binding=FragmentFeedFormulationBinding.inflate(inflater, container, false);
      View root=binding.getRoot();



      return  root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}