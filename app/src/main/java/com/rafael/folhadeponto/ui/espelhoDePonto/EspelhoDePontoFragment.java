package com.rafael.folhadeponto.ui.espelhoDePonto;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.rafael.folhadeponto.Registro;
import com.rafael.folhadeponto.RegistroAdapter;
import com.rafael.folhadeponto.databinding.FragmentEspelhoDePontoBinding;

public class EspelhoDePontoFragment extends Fragment {

    private EspelhoDePontoViewModel mViewModel;
    private FragmentEspelhoDePontoBinding binding;
    private RegistroAdapter adapter;
    private ArrayList<Registro> registros;
    private DatabaseReference databaseReference;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EspelhoDePontoViewModel espelhoDePontoViewModel =
                new ViewModelProvider(this).get(EspelhoDePontoViewModel.class);

        binding = FragmentEspelhoDePontoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        registros = new ArrayList<>();
        adapter = new RegistroAdapter(registros);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(adapter);

        carregarPontos();

        return root;
    }

    private void carregarPontos() {

        String usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Recupera o UID do usuário autenticado
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("pontos/usuarios/" + usuarioId);

        // Recupera os dados do Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    registros.clear(); // Limpa a lista antes de adicionar novos dados
                    for (DataSnapshot registroSnapshot : snapshot.getChildren()) {
                        Registro registro = registroSnapshot.getValue(Registro.class);
                        registros.add(registro);
                    }
                    adapter.notifyDataSetChanged(); // Atualiza o RecyclerView
                } else {
                    Log.d("FirebaseData", "Nenhum dado encontrado para o usuário.");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("FirebaseError", "Erro ao carregar pontos: " + error.getMessage());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}