package com.rafael.folhadeponto.ui.configuracoes;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rafael.folhadeponto.MenuAdapter;
import com.rafael.folhadeponto.R;
import com.rafael.folhadeponto.Registro;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ConfiguracoesFragment extends Fragment {

    private ConfiguracoesViewModel mViewModel;
    private RecyclerView recyclerView;
    private MenuAdapter adapter;
    private List<String> menuItems;

    public static ConfiguracoesFragment newInstance() {
        return new ConfiguracoesFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_configuracoes, container, false);

        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        menuItems = new ArrayList<>();
        menuItems.add("Exportar Relatório de Registro de Ponto");
        menuItems.add("Notificações");
        menuItems.add("Privacidade e Segurança");

        // Configura o Adapter com um listener para os cliques
        adapter = new MenuAdapter(menuItems, item -> {

            switch (item) {
                case "Exportar Relatório de Registro de Ponto":
                    exportarRelatorioDePonto();
                    break;
                case "Notificações":
                    Toast.makeText(getContext(), "Clicou em: Notificações", Toast.LENGTH_SHORT).show();
                    break;
                case "Privacidade e Segurança":
                    Toast.makeText(getContext(), "Clicou em: Privacidade e Segurança", Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        recyclerView.setAdapter(adapter);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ConfiguracoesViewModel.class);
        // TODO: Use the ViewModel
    }

    private void exportarRelatorioDePonto() {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Usuário não autenticado.", Toast.LENGTH_SHORT).show();
            return; // Sai do método se o usuário não estiver autenticado
        }

        String usuarioId = currentUser.getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("pontos/usuarios/" + usuarioId);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<Registro> registros = new ArrayList<>();

                    // Itera sobre os registros no nó do usuário
                    for (DataSnapshot registroSnapshot : dataSnapshot.getChildren()) {
                        Registro registro = registroSnapshot.getValue(Registro.class);
                        registros.add(registro);
                    }

                    // Gera o relatório Excel após obter os registros
                    gerarRelatorioExcel(registros);
                } else {
                    Toast.makeText(getContext(), "Nenhum registro encontrado.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Erro ao recuperar registros: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void gerarRelatorioExcel(List<Registro> registros) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Registros de Ponto");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Tipo");
        headerRow.createCell(1).setCellValue("Data e Hora");

        int rowNum = 1;
        for (Registro registro : registros) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(registro.getTipo());
            row.createCell(1).setCellValue(registro.getDataHora());
        }

        // Salvando o arquivo em armazenamento externo
        try {
            File file = new File(requireContext().getExternalFilesDir(null), "RegistrosDePonto.xlsx");
            FileOutputStream fos = new FileOutputStream(file);
            workbook.write(fos);
            fos.close();
            workbook.close();
            Toast.makeText(getContext(), "Relatório exportado para: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(getContext(), "Erro ao salvar o relatório.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private Uri salvarNoScopedStorage(String fileName, XSSFWorkbook workbook) throws Exception {
        Uri fileUri = null;

        // Verifica se estamos em Scoped Storage (Android 10+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.Files.FileColumns.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            contentValues.put(MediaStore.Files.FileColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/RegistrosDePonto.xlsx");

            Uri uri = requireContext().getContentResolver().insert(MediaStore.Files.getContentUri("external"), contentValues);
            if (uri != null) {
                try (OutputStream outputStream = requireContext().getContentResolver().openOutputStream(uri)) {
                    workbook.write(outputStream);
                    workbook.close();
                    fileUri = uri;
                }
            }
        } else {
            // Para dispositivos anteriores ao Android 10
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "RegistrosDePonto.xlsx");
            if (!dir.exists()) dir.mkdirs();

            File file = new File(dir, fileName);
            try (OutputStream outputStream = new FileOutputStream(file)) {
                workbook.write(outputStream);
                workbook.close();
                fileUri = Uri.fromFile(file);
            }
        }
        return fileUri;
    }
}