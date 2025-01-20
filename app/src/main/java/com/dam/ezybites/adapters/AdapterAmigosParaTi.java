package com.dam.ezybites.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dam.ezybites.R;
import com.dam.ezybites.pojos.RecetaConAutor;

import java.util.List;

public class AdapterAmigosParaTi extends RecyclerView.Adapter<AdapterAmigosParaTi.ItemVH> {

    private Context context;
    private List<RecetaConAutor> recetasList;

    public AdapterAmigosParaTi(Context context, List<RecetaConAutor> recetasList) {
        this.context = context;
        this.recetasList = recetasList;
    }

    @NonNull
    @Override
    public ItemVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.tarjeta_amigos, parent, false);
        return new ItemVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemVH holder, int position) {
        RecetaConAutor recetaConAutor = recetasList.get(position);

        Glide.with(context).load(recetaConAutor.getReceta().getUrl_foto()).into(holder.foto);
        holder.texto.setText(recetaConAutor.getUsername()); // Mostrar autor
        //holder.texto.setText(recetaConAutor.getReceta().getNombre()); Mostrar el nombre de la receta
        Glide.with(context).load(recetaConAutor.getUrlFotoPerfilAutor()).into(holder.pfp);
        Log.d("Glide", "Url: " + recetaConAutor.getReceta().getUrl_foto());

        switch(recetaConAutor.getReceta().getTipo()) {
            case 1:
                holder.type.setImageResource(R.drawable.icon_type_1);
                break;
            case 2:
                holder.type.setImageResource(R.drawable.icon_type_2);
                break;
            case 3:
                holder.type.setImageResource(R.drawable.icon_type_3);
                break;
            default:
                holder.type.setImageResource(R.drawable.icon_type_default);
        }
    }

    @Override
    public int getItemCount() {
        return recetasList.size();
    }

    public static class ItemVH extends RecyclerView.ViewHolder {
        ImageView foto, type, pfp;
        TextView texto;

        public ItemVH(@NonNull View itemView) {
            super(itemView);
            foto = itemView.findViewById(R.id.tarjeta_amigos_foto);
            type = itemView.findViewById(R.id.tarjeta_amigos_type);
            pfp = itemView.findViewById(R.id.tarjeta_amigos_pfp);
            texto = itemView.findViewById(R.id.tarjeta_amigos_text);
        }
    }
}
