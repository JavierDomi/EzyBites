package com.dam.ezybites.ui.home.innerFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dam.ezybites.R;
import com.dam.ezybites.adapters.AdapterAmigosParaTi;
import com.dam.ezybites.pojos.RecetaConAutor;
import com.dam.ezybites.pojos.Receta;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class home_para_ti extends Fragment {

    private RecyclerView recyclerView;
    private AdapterAmigosParaTi adapter;
    private List<RecetaConAutor> recetasList;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    public home_para_ti() {
        // Required empty public constructor
    }

    public static home_para_ti newInstance() {
        return new home_para_ti();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_para_ti, container, false);

        recyclerView = view.findViewById(R.id.rv_amigos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        recetasList = new ArrayList<>();
        adapter = new AdapterAmigosParaTi(getContext(), recetasList);
        recyclerView.setAdapter(adapter);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d("Firebase", "Usuario autenticado: " + currentUser.getEmail());
            Log.d("Firebase", "Usuario autenticado: " + currentUser.getUid());
            cargarRecetasAmigos(currentUser.getUid());
        } else {
            Log.e("Firebase", "No hay usuario autenticado");
        }

        return view;
    }

    private void cargarRecetasAmigos(String userId) {
        DatabaseReference amigosRef = mDatabase.child("Usuarios").child(userId).child("amigos");

        amigosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Log.d("Firebase", "No se encontraron amigos");
                    return;
                }

                for (DataSnapshot amigoSnapshot : dataSnapshot.getChildren()) {
                    String amigoId = amigoSnapshot.getValue(String.class);
                    if (amigoId != null) {
                        cargarRecetasPublicadasAmigo(amigoId);
                    } else {
                        Log.e("Firebase", "ID de amigo nulo");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error al cargar amigos: " + databaseError.getMessage());
                Toast.makeText(getContext(), "Error al cargar recetas de amigos. Por favor, intenta de nuevo.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarRecetasPublicadasAmigo(String amigoId) {
        DatabaseReference recetasPublicadasRef = mDatabase.child("Usuarios").child(amigoId).child("recetas_publicadas");

        recetasPublicadasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot recetaSnapshot : dataSnapshot.getChildren()) {
                    String recetaId = recetaSnapshot.getValue(String.class);
                    if (recetaId != null) {
                        cargarDetallesReceta(recetaId, amigoId);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error al cargar recetas publicadas: " + databaseError.getMessage());
            }
        });
    }

    private void cargarDetallesReceta(String recetaId, String autorId) {
        DatabaseReference recetaRef = mDatabase.child("Recetas").child(recetaId);
        DatabaseReference autorRef = mDatabase.child("Usuarios").child(autorId);

        recetaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot recetaSnapshot) {
                if (recetaSnapshot.exists()) {
                    Receta receta = recetaSnapshot.getValue(Receta.class);
                    if (receta != null) {
                        autorRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot autorSnapshot) {
                                String urlFotoPerfilAutor = autorSnapshot.child("url_foto_perfil").getValue(String.class);
                                String username = autorSnapshot.child("username").getValue(String.class);

                                if (urlFotoPerfilAutor != null && username != null) {
                                    RecetaConAutor recetaConAutor = new RecetaConAutor(receta, urlFotoPerfilAutor, username);
                                    recetasList.add(recetaConAutor);
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Log.e("Firebase", "Datos de autor incompletos para ID: " + autorId);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e("Firebase", "Error al cargar datos del autor: " + databaseError.getMessage());
                            }
                        });
                    } else {
                        Log.e("Firebase", "Datos de receta incompletos para ID: " + recetaId);
                    }
                } else {
                    Log.e("Firebase", "No se encontraron datos para la receta con ID: " + recetaId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error al cargar detalles de la receta: " + databaseError.getMessage());
            }
        });
    }


    private void cargarFotoPerfilAutor(Receta receta, String autorId) {
        DatabaseReference autorRef = mDatabase.child("Usuarios").child(autorId);

        autorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String urlFotoPerfil = dataSnapshot.child("url_foto_perfil").getValue(String.class);
                String username = dataSnapshot.child("username").getValue(String.class);

                if (urlFotoPerfil != null && username != null) {
                    RecetaConAutor recetaConAutor = new RecetaConAutor(receta, urlFotoPerfil, username);
                    recetasList.add(recetaConAutor);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("Firebase", "Datos de autor incompletos para ID: " + autorId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error al cargar datos del autor: " + databaseError.getMessage());
            }
        });
    }

}
