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
import com.dam.ezybites.pojos.Amigo;
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
    private List<Amigo> amigosList;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    public home_para_ti() {
        // Constructor vacío requerido
    }

    public static home_para_ti newInstance() {
        return new home_para_ti();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase
                .getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_para_ti, container, false);

        recyclerView = view.findViewById(R.id.rv_amigos);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        amigosList = new ArrayList<>();
        adapter = new AdapterAmigosParaTi(getContext(), amigosList);
        recyclerView.setAdapter(adapter);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d("Firebase", "Usuario autenticado: " + currentUser.getEmail());
            Log.d("Firebase", "Usuario autenticado: " + currentUser.getUid());
            cargarAmigosDelUsuario(currentUser.getUid());
        } else {
            Log.e("Firebase", "No hay usuario autenticado");
        }

        return view;
    }

    private void cargarAmigosDelUsuario(String userId) {
        DatabaseReference amigosRef = mDatabase.child("Usuarios").child(userId).child("amigos");

        amigosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Log.d("Firebase", "No se encontraron amigos");
                    return;
                }

                amigosList.clear();
                for (DataSnapshot amigoSnapshot : dataSnapshot.getChildren()) {
                    String amigoId = amigoSnapshot.getValue(String.class);
                    if (amigoId != null) {
                        cargarDetallesAmigo(amigoId);
                    } else {
                        Log.e("Firebase", "ID de amigo nulo");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error al cargar amigos: " + databaseError.getMessage());
                Toast.makeText(getContext(), "Error al cargar amigos. Por favor, intenta de nuevo.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarDetallesAmigo(String amigoId) {
        mDatabase.child("Usuarios").child(amigoId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue(String.class);
                    String urlFotoPerfil = dataSnapshot.child("url_foto_perfil").getValue(String.class);

                    if (username != null && urlFotoPerfil != null) {
                        Amigo amigo = new Amigo(amigoId, username, urlFotoPerfil);
                        amigosList.add(amigo);
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("Firebase", "Datos de amigo incompletos para ID: " + amigoId);
                    }
                } else {
                    Log.e("Firebase", "No se encontraron datos para el amigo con ID: " + amigoId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error al cargar detalles del amigo: " + databaseError.getMessage());
            }
        });
    }
}
