package com.dam.ezybites.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dam.ezybites.R;
import com.dam.ezybites.pojos.Amigo;

import java.util.List;

public class AdapterAmigosParaTi extends RecyclerView.Adapter<AdapterAmigosParaTi.ItemVH> {

    private Context context;
    private List<Amigo> amigosList;

    public AdapterAmigosParaTi(Context context, List<Amigo> amigosList) {
        this.context = context;
        this.amigosList = amigosList;
    }

    @NonNull
    @Override
    public ItemVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.tarjeta_amigos, parent, false);
        return new ItemVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemVH holder, int position) {
        Amigo amigo = amigosList.get(position);

        Glide.with(context).load(amigo.getFotoUrl()).into(holder.foto);
        holder.texto.setText(amigo.getNombre());

    }

    @Override
    public int getItemCount() {
        return amigosList.size();
    }

    public static class ItemVH extends RecyclerView.ViewHolder {

        CardView tarjetaAmigos;
        ImageView foto, type, pfp;
        TextView texto;

        public ItemVH(@NonNull View itemView) {
            super(itemView);

            tarjetaAmigos = itemView.findViewById(R.id.tajeta_amigos_id);
            foto = itemView.findViewById(R.id.tarjeta_amigos_foto);
            type = itemView.findViewById(R.id.tarjeta_amigos_type);
            pfp = itemView.findViewById(R.id.tarjeta_amigos_pfp);
            texto = itemView.findViewById(R.id.tarjeta_amigos_text);
        }
    }
}
