package com.rafael.folhadeponto.ui.sair;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.rafael.folhadeponto.databinding.FragmentSairBinding;

public class SairFragment extends Fragment {

    private FragmentSairBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SairViewModel sairViewModel =
                new ViewModelProvider(this).get(SairViewModel.class);

        binding = FragmentSairBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSair;
        sairViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

}