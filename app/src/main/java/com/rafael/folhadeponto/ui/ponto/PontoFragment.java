package com.rafael.folhadeponto.ui.ponto;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rafael.folhadeponto.databinding.FragmentPontoBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class PontoFragment extends Fragment {

    private FragmentPontoBinding binding;
    private DatabaseReference databaseReference;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        PontoViewModel pontoViewModel =
                new ViewModelProvider(this).get(PontoViewModel.class);

        binding = FragmentPontoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        databaseReference = FirebaseDatabase.getInstance().getReference("pontos");

        binding.btMarcarHora.setOnClickListener(v -> registrarPonto());

        final TextView textView = binding.tvStatus;
        pontoViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    private void registrarPonto() {
        String usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference usuarioRef = databaseReference.child("usuarios").child(usuarioId);

        // Busca o último registro no Firebase
        usuarioRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String tipo;
                String dataAtual = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                if (snapshot.exists()) {
                    // Obtém o último registro
                    DataSnapshot ultimoRegistro = snapshot.getChildren().iterator().next();
                    String tipoUltimo = ultimoRegistro.child("tipo").getValue(String.class);
                    String dataHoraUltimo = ultimoRegistro.child("dataHora").getValue(String.class);

                    try {
                        // Extrai a data do último registro
                        String dataUltimaBatida = dataHoraUltimo.split(" ")[0];

                        // Verifica se o último registro é do mesmo dia
                        if (dataUltimaBatida.equals(dataAtual)) {
                            // Alterna entre "Entrada" e "Saída" para o mesmo dia
                            tipo = "Saída".equals(tipoUltimo) ? "Entrada" : "Saída";
                        } else {
                            // Se for um dia diferente, inicia com "Entrada"
                            tipo = "Entrada";
                        }
                    } catch (Exception e) {
                        // Em caso de erro ao processar o último registro, inicia com "Entrada"
                        tipo = "Entrada";
                    }
                } else {
                    // Se não houver registros, o primeiro será "Entrada"
                    tipo = "Entrada";
                }
                salvarRegistroPonto(tipo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Erro ao buscar último registro.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void salvarRegistroPonto(String tipo) {
        String usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String dataHora = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

        // Cria o mapa de dados
        HashMap<String, String> registro = new HashMap<>();
        registro.put("tipo", tipo);
        registro.put("dataHora", dataHora);

        // Salva no Firebase
        databaseReference.child("usuarios").child(usuarioId).push().setValue(registro)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Atualiza o texto de status
                        binding.tvStatus.setText(tipo + " registrada em: " + dataHora);

                        // Atualiza o texto do botão para o próximo tipo
                        binding.btMarcarHora.setText("Entrada".equals(tipo) ? "Registrar Saída" : "Registrar Entrada");
                    } else {
                        binding.tvStatus.setText("Erro ao registrar " + tipo);
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}