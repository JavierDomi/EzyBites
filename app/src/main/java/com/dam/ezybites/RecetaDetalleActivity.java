package com.dam.ezybites;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.dam.ezybites.pojos.Receta;
import com.dam.ezybites.pojos.RecetaConAutor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RecetaDetalleActivity extends AppCompatActivity {

    private ImageView fotoReceta, fotoPerfilAutor;
    private TextView tituloReceta, duracionReceta, tipoReceta, autorReceta, ingredientesReceta, pasosReceta;
    private Button btnGuardarReceta;
    private Spinner tagsSpinner;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String recetaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receta_completa);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        inicializarVistas();

        RecetaConAutor recetaConAutor = getIntent().getParcelableExtra("receta");
        if (recetaConAutor != null && recetaConAutor.getReceta() != null) {
            recetaId = recetaConAutor.getReceta().getId();
            mostrarDatosReceta(recetaConAutor);
            verificarRecetaGuardada(recetaId);
        } else {
            Log.e("RecetaDetalleActivity", "RecetaConAutor or Receta is null");
            Toast.makeText(this, "Error: No se pudo cargar la receta", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void inicializarVistas() {
        fotoReceta = findViewById(R.id.foto_receta);
        tituloReceta = findViewById(R.id.titulo_receta);
        duracionReceta = findViewById(R.id.duracion_receta);
        tipoReceta = findViewById(R.id.tipo_receta);
        autorReceta = findViewById(R.id.autor_receta_detalle);
        fotoPerfilAutor = findViewById(R.id.foto_de_perfil_receta);
        btnGuardarReceta = findViewById(R.id.btn_guardar_receta);
        ingredientesReceta = findViewById(R.id.ingredientes_receta_detalle);
        pasosReceta = findViewById(R.id.pasos_receta);
        tagsSpinner = findViewById(R.id.tags_spinner);
    }

    private void mostrarDatosReceta(RecetaConAutor recetaConAutor) {
        Receta receta = recetaConAutor.getReceta();

        Glide.with(this).load(receta.getUrl_foto()).into(fotoReceta);
        tituloReceta.setText(receta.getNombre());
        duracionReceta.setText(receta.getDuracion());
        tipoReceta.setText(getTipoReceta(receta.getTipo()));
        findUserNamebyId(recetaConAutor.getReceta().getAutor());
        Glide.with(this).load(recetaConAutor.getUrlFotoPerfilAutor()).into(fotoPerfilAutor);

        StringBuilder ingredientesBuilder = new StringBuilder();
        for (String ingrediente : receta.getIngredientes()) {
            ingredientesBuilder.append("• ").append(ingrediente).append("\n");
        }
        ingredientesReceta.setText(ingredientesBuilder.toString());

        pasosReceta.setText(receta.getPasos());

        ArrayAdapter<String> tagsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, receta.getTags());
        tagsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tagsSpinner.setAdapter(tagsAdapter);
    }

    private String getTipoReceta(int tipo) {
        switch (tipo) {
            case 1:
                return "Vegano";
            case 2:
                return "Vegetariano";
            case 3:
                return "Con carne";
            default:
                return "No especificado";
        }
    }

    private void findUserNamebyId(String autor) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Usuarios").child(autor);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue(String.class);
                    autorReceta.setText("By " + username);
                } else {
                    Log.e("RecetaDetalleActivity", "User not found for ID: " + autor);
                    autorReceta.setText("By Unknown");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("RecetaDetalleActivity", "Error fetching user data: " + databaseError.getMessage());
                autorReceta.setText("By Unknown");
            }
        });
    }

    private void verificarRecetaGuardada(String recetaId) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userRef = mDatabase.child("Usuarios").child(currentUser.getUid())
                    .child("recetas_guardadas").child(recetaId);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean recetaYaGuardada = dataSnapshot.exists();
                    btnGuardarReceta.setEnabled(!recetaYaGuardada);
                    btnGuardarReceta.setText(recetaYaGuardada ? "Receta guardada" : "Guardar receta");
                    btnGuardarReceta.setVisibility(View.VISIBLE);
                    btnGuardarReceta.setOnClickListener(v -> guardarReceta(recetaId));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("Firebase", "Error al verificar receta guardada", databaseError.toException());
                }
            });
        } else {
            btnGuardarReceta.setVisibility(View.GONE);
        }
    }

    private void guardarReceta(String recetaId) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userRef = mDatabase.child("Usuarios").child(currentUser.getUid())
                    .child("recetas_guardadas");

            userRef.child(recetaId).setValue(true)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(RecetaDetalleActivity.this, "Receta guardada", Toast.LENGTH_SHORT).show();
                        btnGuardarReceta.setEnabled(false);
                        btnGuardarReceta.setText("Receta guardada");
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(RecetaDetalleActivity.this, "Error al guardar la receta", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show();
        }
    }
}
