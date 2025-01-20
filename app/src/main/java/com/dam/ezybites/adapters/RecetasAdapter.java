package com.dam.ezybites.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dam.ezybites.R;
import com.dam.ezybites.pojos.Receta;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RecetasAdapter extends RecyclerView.Adapter<RecetasAdapter.RecetaViewHolder> {

    private List<Receta> recetas;
    private Context context;
    private DatabaseReference mDatabase;

    public RecetasAdapter(Context context, List<Receta> recetas) {
        this.context = context;
        this.recetas = recetas != null ? recetas : new ArrayList<>();
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public RecetaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_receta, parent, false);
        return new RecetaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecetaViewHolder holder, int position) {
        Receta receta = recetas.get(position);

        // Cargar imagen de la receta
        Glide.with(context)
                .load(receta.getUrl_foto())
                .placeholder(R.drawable.imagen_default) // Placeholder por si la carga falla
                .into(holder.fotoReceta);

        holder.duracionReceta.setText(receta.getDuracion() != null ? receta.getDuracion() : "Duración no disponible");
        holder.tipoReceta.setText(receta.getTipo() != 0 ? String.valueOf(receta.getTipo()) : "Tipo desconocido");

        List<String> tags = receta.getTags();

        holder.tituloReceta.setText(receta.getNombre() != null ? receta.getNombre() : "Nombre no disponible");
        cargarNombreAutor(receta.getAutor(), holder.autorReceta);

        cargarFotoPerfilAutor(receta.getAutor(), holder.fotoDePerfilReceta);
    }


    private void cargarFotoPerfilAutor(String autorId, final ImageView imageView) {
        //Glide.with(context).load(recetaConAutor.getReceta().getUrl_foto()).into(holder.foto);
        mDatabase.child("Usuarios").child(autorId).child("url_foto_perfil")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String urlFotoPerfil = dataSnapshot.getValue(String.class);
                        if (urlFotoPerfil != null && !urlFotoPerfil.isEmpty() && URLUtil.isValidUrl(urlFotoPerfil)) {
                            Glide.with(context)
                                    .load(urlFotoPerfil)
                                    .circleCrop()
                                    .into(imageView);
                        } else {
                            Glide.with(context)
                                    .load(R.drawable.imagen_default)
                                    .circleCrop()
                                    .into(imageView);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Glide.with(context)
                                .load(R.drawable.imagen_default)
                                .circleCrop()
                                .into(imageView);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return recetas.size();
    }

    public void setRecetas(List<Receta> recetas) {
        this.recetas = recetas != null ? recetas : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class RecetaViewHolder extends RecyclerView.ViewHolder {
        ImageView fotoReceta;
        TextView duracionReceta;
        TextView tipoReceta;
        TextView procedenciaReceta;
        TextView tituloReceta;
        TextView autorReceta;
        ImageView fotoDePerfilReceta;

        RecetaViewHolder(View itemView) {
            super(itemView);
            fotoReceta = itemView.findViewById(R.id.foto_receta);
            duracionReceta = itemView.findViewById(R.id.duracion_receta);
            tipoReceta = itemView.findViewById(R.id.tipo_receta);
            procedenciaReceta = itemView.findViewById(R.id.procedencia_receta);
            tituloReceta = itemView.findViewById(R.id.titulo_receta);
            autorReceta = itemView.findViewById(R.id.autor_receta);
            fotoDePerfilReceta = itemView.findViewById(R.id.foto_de_perfil_receta);
        }
    }

    private void cargarNombreAutor(String autorId, final TextView textView) {
        if (autorId == null || autorId.isEmpty()) {
            textView.setText("Autor desconocido");
            return;
        }

        mDatabase.child("Usuarios").child(autorId).child("username")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String nombreAutor = dataSnapshot.getValue(String.class);
                        if (nombreAutor != null && !nombreAutor.isEmpty()) {
                            textView.setText("By " + nombreAutor);
                        } else {
                            textView.setText("Autor desconocido");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        textView.setText("Error al cargar el autor");
                    }
                });
    }

}
