package com.rafael.folhadeponto.ui.mensagens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.rafael.folhadeponto.databinding.FragmentMensagensBinding;

public class MensagensFragment extends Fragment {

    private FragmentMensagensBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MensagensViewModel mensagensViewModel =
                new ViewModelProvider(this).get(MensagensViewModel.class);

        binding = FragmentMensagensBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSlideshow;
        mensagensViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}