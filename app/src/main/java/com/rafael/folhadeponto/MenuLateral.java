package com.rafael.folhadeponto;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rafael.folhadeponto.databinding.ActivityMenuLateralBinding;
import com.rafael.folhadeponto.ui.ajuda.AjudaFragment;
import com.rafael.folhadeponto.ui.configuracoes.ConfiguracoesFragment;
import com.rafael.folhadeponto.ui.espelhoDePonto.EspelhoDePontoFragment;
import com.rafael.folhadeponto.ui.home.HomeFragment;
import com.rafael.folhadeponto.ui.mensagens.MensagensFragment;
import com.rafael.folhadeponto.ui.ponto.PontoFragment;
import com.rafael.folhadeponto.ui.rh.RhFragment;

public class MenuLateral extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMenuLateralBinding binding;

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    private TextView nomeUsuario,emailUsuario;
    private ImageView fotoPerfil;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_STORAGE_PERMISSION = 2;

    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        binding = ActivityMenuLateralBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMenuLateral.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Carrega o HomeFragment automaticamente ao iniciar
        if (savedInstanceState == null) {
            navigateToFragment(new HomeFragment(), "Home");
        }

        atualizarNavHeader(navigationView);

        binding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_ajuda) {
                    //Toast.makeText(MenuLateral.this, "Menu Ajuda", Toast.LENGTH_SHORT).show();
                    navigateToFragment(new AjudaFragment(), "Ajuda");
                } else if (item.getItemId() == R.id.nav_configuracoes) {
                    //Toast.makeText(MenuLateral.this, "Configurações", Toast.LENGTH_SHORT).show();
                    navigateToFragment(new ConfiguracoesFragment(), "Configurações");
                } else if (item.getItemId() == R.id.nav_home) {
                    //Toast.makeText(MenuLateral.this, "Home", Toast.LENGTH_SHORT).show();
                    navigateToFragment(new HomeFragment(), "Home");
                } else if (item.getItemId() == R.id.nav_espelho_de_ponto) {
                    //Toast.makeText(MenuLateral.this, "Espelho de Ponto", Toast.LENGTH_SHORT).show();
                    navigateToFragment(new EspelhoDePontoFragment(), "Espelho de Ponto");
                } else if (item.getItemId() == R.id.nav_mensagens) {
                    //Toast.makeText(MenuLateral.this, "Mensagens", Toast.LENGTH_SHORT).show();
                    navigateToFragment(new MensagensFragment(), "Mensagens");
                } else if (item.getItemId() == R.id.nav_ponto) {
                    //Toast.makeText(MenuLateral.this, "Ponto", Toast.LENGTH_SHORT).show();
                    navigateToFragment(new PontoFragment(), "Ponto");
                } else if (item.getItemId() == R.id.nav_rh) {
                    //Toast.makeText(MenuLateral.this, "RH", Toast.LENGTH_SHORT).show();
                    navigateToFragment(new RhFragment(), "RH");
                } else if (item.getItemId() == R.id.nav_sair) {
                    logout();
                }

                // Fechar o drawer após a interação
                binding.drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    private void atualizarNavHeader(NavigationView navigationView) {
        // Obtém o cabeçalho
        View headerView = navigationView.getHeaderView(0);

        nomeUsuario = headerView.findViewById(R.id.nomeUsuario);
        emailUsuario = headerView.findViewById(R.id.emailUsuario);
        fotoPerfil = headerView.findViewById(R.id.fotoPerfil);

        fotoPerfil.setOnClickListener(view -> {
            // Exibe opções de escolher entre galeria ou tirar uma foto
            AlertDialog.Builder builder = new AlertDialog.Builder(MenuLateral.this);
            builder.setTitle("Escolha uma opção")
                    .setItems(new CharSequence[] {"Tirar Foto", "Escolher da Galeria", "Excluir Foto"},
                            (dialog, which) -> {
                                if (which == 0) {
                                    openCamera();
                                } else if (which == 1) {
                                    openGallery();
                                } else if (which == 2) {
                                    excluirFotoPerfil();
                                }
                            });
            builder.create().show();
        });

        // Obtém o ID do usuário logado
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // Atualiza o e-mail no cabeçalho
            emailUsuario.setText(currentUser.getEmail());

            String usuarioId = currentUser.getUid();
            firestore.collection("Usuarios").document(usuarioId).get().addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Atualiza o nome do usuário
                            String nome = documentSnapshot.getString("nome");
                            nomeUsuario.setText(nome);

                            // Atualiza a foto de perfil
                            String imageUrl = documentSnapshot.getString("fotoPerfil");
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                Glide.with(MenuLateral.this)
                                        .load(imageUrl)
                                        .placeholder(R.drawable.ic_user)
                                        .into(fotoPerfil);
                            }
                        }
            }).addOnFailureListener(e -> {
                Toast.makeText(MenuLateral.this, "Erro ao carregar informações do usuário", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openCamera() {
        // Cria um Intent para capturar uma imagem
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Verifica se existe uma atividade que pode lidar com a intenção
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // Cria um arquivo temporário para armazenar a imagem capturada
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
                Toast.makeText(this, "Erro ao criar arquivo de imagem", Toast.LENGTH_SHORT).show();
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(
                        this,
                        "com.rafael.folhadeponto.fileprovider",
                        photoFile
                );
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        } else {
            Toast.makeText(this, "Câmera não disponível", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        // Cria um nome único para o arquivo
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        // Diretório padrão para salvar imagens
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // Cria o arquivo
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Salva o caminho do arquivo para uso posterior
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri imageUri = null;

            if (requestCode == CAMERA_REQUEST) {
                File file = new File(currentPhotoPath);
                imageUri = Uri.fromFile(file);
            } else if (requestCode == PICK_IMAGE_REQUEST && data != null) {
                imageUri = data.getData();
            }

            if (imageUri != null) {
                // Permite acesso persistente ao arquivo
                getContentResolver().takePersistableUriPermission(
                        imageUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                );
                uploadFoto(imageUri);
            }
        }
    }

    private void uploadFoto(Uri imageUri) {
        String usuarioId = auth.getCurrentUser().getUid();
        firestore.collection("Usuarios").document(usuarioId)
                .update("fotoPerfil", imageUri.toString())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(MenuLateral.this, "Foto de perfil atualizada!", Toast.LENGTH_SHORT).show();
                    atualizarNavHeader(binding.navView);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MenuLateral.this, "Erro ao atualizar foto", Toast.LENGTH_SHORT).show();
                });
    }

    private void excluirFotoPerfil() {
        String usuarioId = auth.getCurrentUser().getUid();

        firestore.collection("Usuarios").document(usuarioId)
                .update("fotoPerfil", null)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(MenuLateral.this, "Foto de perfil excluída!", Toast.LENGTH_SHORT).show();

                    // Atualiza a imagem no Nav Header com a padrão
                    fotoPerfil.setImageResource(R.drawable.ic_user);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MenuLateral.this, "Erro ao excluir foto", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permissão de câmera concedida
                } else {
                    Toast.makeText(this, "Permissão de câmera negada", Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_STORAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permissão de armazenamento concedida
                } else {
                    Toast.makeText(this, "Permissão de armazenamento negada", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void logout() {
        Toast.makeText(this, "Logout efetuado com sucesso", Toast.LENGTH_SHORT).show();

        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, FormLogin.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null; // Boa prática para evitar vazamento de memória
    }

    private void navigateToFragment(Fragment fragment, String title) {
        // Troca o fragment atual pelo selecionado
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment_content_menu_lateral, fragment);
        transaction.commit();

        // Atualiza o título da Toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }
}

