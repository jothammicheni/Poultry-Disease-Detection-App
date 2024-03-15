package com.example.layersdiseasedetection.ui.Logout;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.layersdiseasedetection.databinding.FragmentLogoutBinding;
public class LogoutFragment extends Fragment {
    private FragmentLogoutBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
      binding=FragmentLogoutBinding.inflate(inflater,container,false);
      View root=binding.getRoot();



      return root;

    }


}