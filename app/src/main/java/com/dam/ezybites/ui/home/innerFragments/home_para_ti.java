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
import com.dam.ezybites.adapters.AdapterTrending;
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

    private RecyclerView rvAmigos, rvTrending;
    private AdapterAmigosParaTi adapterAmigos;
    private AdapterTrending adapterTrending;
    private List<RecetaConAutor> recetasAmigosList, recetasTrendingList;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_para_ti, container, false);

        initializeRecyclerViews(view);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            cargarRecetasAmigos(currentUser.getUid());
            cargarRecetasTrending();
        } else {
            Log.e("Firebase", "No hay usuario autenticado");
        }

        return view;
    }

    private void initializeRecyclerViews(View view) {
        rvAmigos = view.findViewById(R.id.rv_amigos);
        rvTrending = view.findViewById(R.id.rv_trending);

        LinearLayoutManager layoutManagerHorizontal = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvAmigos.setLayoutManager(layoutManagerHorizontal);
        rvTrending.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        recetasAmigosList = new ArrayList<>();
        recetasTrendingList = new ArrayList<>();

        adapterAmigos = new AdapterAmigosParaTi(getContext(), recetasAmigosList);
        adapterTrending = new AdapterTrending(getContext(), recetasTrendingList);

        rvAmigos.setAdapter(adapterAmigos);
        rvTrending.setAdapter(adapterTrending);
    }

    private void cargarRecetasAmigos(String userId) {
        mDatabase.child("Usuarios").child(userId).child("amigos").addListenerForSingleValueEvent(new ValueEventListener() {
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
        mDatabase.child("Usuarios").child(amigoId).child("recetas_publicadas").addListenerForSingleValueEvent(new ValueEventListener() {
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
        mDatabase.child("Recetas").child(recetaId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot recetaSnapshot) {
                if (recetaSnapshot.exists()) {
                    Receta receta = recetaSnapshot.getValue(Receta.class);
                    if (receta != null) {
                        receta.setId(recetaId);
                        cargarDatosAutor(receta, autorId);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error al cargar detalles de la receta: " + databaseError.getMessage());
            }
        });
    }

    private void cargarDatosAutor(Receta receta, String autorId) {
        mDatabase.child("Usuarios").child(autorId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot autorSnapshot) {
                String urlFotoPerfilAutor = autorSnapshot.child("url_foto_perfil").getValue(String.class);
                String username = autorSnapshot.child("username").getValue(String.class);

                if (urlFotoPerfilAutor != null && username != null) {
                    RecetaConAutor recetaConAutor = new RecetaConAutor(receta, urlFotoPerfilAutor, username);
                    recetasAmigosList.add(recetaConAutor);
                    adapterAmigos.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error al cargar datos del autor: " + databaseError.getMessage());
            }
        });
    }

    private void cargarRecetasTrending() {
        mDatabase.child("Recetas").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot recetaSnapshot : dataSnapshot.getChildren()) {

                    List<String> tags = (List<String>) recetaSnapshot.child("tags").getValue();

                    // Verificar si "trending" está en el array de tags
                    if (tags != null && tags.contains("trending")) {
                        Receta receta = recetaSnapshot.getValue(Receta.class);
                        if (receta != null) {
                            cargarDatosAutorTrending(receta, recetaSnapshot.getKey());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error al cargar recetas trending: " + databaseError.getMessage());
            }
        });
    }



    private void cargarDatosAutorTrending(Receta receta, String recetaId) {
        mDatabase.child("Usuarios").child(receta.getAutor()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot autorSnapshot) {
                String urlFotoPerfilAutor = autorSnapshot.child("url_foto_perfil").getValue(String.class);
                String username = autorSnapshot.child("username").getValue(String.class);

                if (urlFotoPerfilAutor != null && username != null) {
                    receta.setId(recetaId);
                    RecetaConAutor recetaConAutor = new RecetaConAutor(receta, urlFotoPerfilAutor, username);
                    recetasTrendingList.add(recetaConAutor);
                    adapterTrending.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error al cargar datos del autor trending: " + databaseError.getMessage());
            }
        });
    }
}
