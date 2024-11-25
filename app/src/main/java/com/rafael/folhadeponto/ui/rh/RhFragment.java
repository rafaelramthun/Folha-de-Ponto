package com.rafael.folhadeponto.ui.rh;

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

public class RhFragment extends Fragment {

    private RhViewModel mViewModel;
    private RecyclerView recyclerView;
    private MenuAdapter adapter;
    private List<String> menuItems;

    public static RhFragment newInstance() {
        return new RhFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_rh, container, false);

        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        menuItems = new ArrayList<>();
        menuItems.add("Envelope de Pagamento");
        menuItems.add("Informe de Rendimentos");
        menuItems.add("Enviar Atestado");
        menuItems.add("Férias");

        adapter = new MenuAdapter(menuItems, item -> {

            Toast.makeText(getContext(), "Clicou em: " + item, Toast.LENGTH_SHORT).show();

            switch (item) {
                case "Envelope de Pagamento":
                    // Ação para "Envelope de Pagamento"
                    break;
                case "Informe de Rendimentos":
                    // Ação para "Informe de Rendimentos"
                    break;
                case "Enviar Atestado":
                    // Ação para "Enviar Atestado"
                    break;
                case "Férias":
                // Ação para "Férias"
                break;
            }
        });

        recyclerView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(RhViewModel.class);
        // TODO: Use the ViewModel
    }
}