package com.rafael.folhadeponto.ui.pontoEletronico;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.rafael.folhadeponto.databinding.FragmentPontoEletronicoBinding;

public class PontoEletronicoFragment extends Fragment {

    private FragmentPontoEletronicoBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PontoEletronicoViewModel pontoEletronicoViewModel =
                new ViewModelProvider(this).get(PontoEletronicoViewModel.class);

        binding = FragmentPontoEletronicoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textGallery;
        pontoEletronicoViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}