package com.rafael.folhadeponto.ui.ajuda;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.rafael.folhadeponto.MenuAdapter;
import com.rafael.folhadeponto.R;

import java.util.ArrayList;
import java.util.List;

public class AjudaFragment extends Fragment {

    private AjudaViewModel mViewModel;
    private RecyclerView recyclerView;
    private MenuAdapter adapter;
    private List<String> menuItems;

    public static AjudaFragment newInstance() {
        return new AjudaFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_ajuda, container, false);

        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        menuItems = new ArrayList<>();
        menuItems.add("FAQ");
        menuItems.add("Sobre");

        // Configura o Adapter com um listener para os cliques
        adapter = new MenuAdapter(menuItems, item -> {

            Toast.makeText(getContext(), "Clicou em: " + item, Toast.LENGTH_SHORT).show();

            switch (item) {
                case "FAQ":
                    // Ação para "FAQ"
                    break;
                case "Sobre":
                    // Ação para "Sobre"
                    break;
            }
        });

        recyclerView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AjudaViewModel.class);
        // TODO: Use the ViewModel
    }
}