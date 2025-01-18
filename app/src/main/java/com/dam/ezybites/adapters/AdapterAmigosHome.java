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

import com.dam.ezybites.R;

public class AdapterAmigosHome extends RecyclerView.Adapter<AdapterAmigosHome.ItemVH> {

    Context context;

    @NonNull
    @Override
    public ItemVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.tarjeta_amigos, parent, false);
        return new ItemVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemVH holder, int position) {

        //rellenar esto lo de dentro de los ()
        holder.foto.setImageDrawable();
        holder.texto.setText();
        holder.pfp.setImageDrawable();
        holder.type.setImageDrawable();
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ItemVH extends RecyclerView.ViewHolder{

        CardView tarjetaAmigos;
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
