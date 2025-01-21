package com.dam.ezybites.ui.recipes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dam.ezybites.R;
import com.dam.ezybites.adapters.RecetasGuardadasAdapter;
import com.dam.ezybites.databinding.FragmentRecipesBinding;
import com.dam.ezybites.pojos.Receta;
import com.dam.ezybites.pojos.RecetaConAutor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RecipesFragment extends Fragment {

    private FragmentRecipesBinding binding;
    private RecyclerView recyclerView;
    private RecetasGuardadasAdapter adapter;
    private List<RecetaConAutor> recetasGuardadasList;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipes, container, false);

        recyclerView = view.findViewById(R.id.rv_recetas_guardadas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recetasGuardadasList = new ArrayList<>();
        adapter = new RecetasGuardadasAdapter(getContext(), recetasGuardadasList);
        recyclerView.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        cargarRecetasGuardadas();

        return view;
    }

    private void cargarRecetasGuardadas() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference recetasGuardadasRef = mDatabase.child("Usuarios").child(userId).child("recetas_guardadas");

            recetasGuardadasRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    recetasGuardadasList.clear();
                    for (DataSnapshot recetaSnapshot : dataSnapshot.getChildren()) {
                        String recetaId = recetaSnapshot.getKey();
                        cargarDetallesReceta(recetaId);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), "Error al cargar recetas guardadas", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void cargarDetallesReceta(String recetaId) {
        DatabaseReference recetaRef = mDatabase.child("Recetas").child(recetaId);
        recetaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Receta receta = dataSnapshot.getValue(Receta.class);
                if (receta != null) {
                    receta.setId(recetaId);
                    cargarDatosAutor(receta);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error al cargar detalles de la receta", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarDatosAutor(Receta receta) {
        DatabaseReference autorRef = mDatabase.child("Usuarios").child(receta.getAutor());
        autorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child("username").getValue(String.class);
                String urlFotoPerfilAutor = dataSnapshot.child("url_foto_perfil").getValue(String.class);

                RecetaConAutor recetaConAutor = new RecetaConAutor(receta, urlFotoPerfilAutor, username);
                recetasGuardadasList.add(recetaConAutor);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Error al cargar datos del autor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}