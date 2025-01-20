package com.dam.ezybites.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dam.ezybites.R;
import com.dam.ezybites.RecetaDetalleActivity;
import com.dam.ezybites.pojos.RecetaConAutor;

import java.util.List;

public class AdapterAmigosParaTi extends RecyclerView.Adapter<AdapterAmigosParaTi.ItemVH> {

    private static final int[] TIPO_ICONS = {
            R.drawable.icon_type_default,
            R.drawable.icon_type_1,
            R.drawable.icon_type_2,
            R.drawable.icon_type_3
    };

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
        holder.texto.setText(recetaConAutor.getUsername());
        Glide.with(context).load(recetaConAutor.getUrlFotoPerfilAutor()).into(holder.pfp);

        int tipoIndex = Math.min(recetaConAutor.getReceta().getTipo(), TIPO_ICONS.length - 1);
        holder.type.setImageResource(TIPO_ICONS[tipoIndex]);

        holder.itemView.setOnClickListener(v -> abrirDetalleReceta(recetaConAutor));
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

    private void abrirDetalleReceta(RecetaConAutor recetaConAutor) {
        Intent intent = new Intent(context, RecetaDetalleActivity.class);
        intent.putExtra("receta", recetaConAutor);
        context.startActivity(intent);
    }
}
