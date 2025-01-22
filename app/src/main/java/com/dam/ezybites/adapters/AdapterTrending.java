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

public class AdapterTrending extends RecyclerView.Adapter<AdapterTrending.ViewHolder> {

    private static final int[] TIPO_ICONS = {
            R.drawable.icon_type_default,
            R.drawable.icon_type_1,
            R.drawable.icon_type_2,
            R.drawable.icon_type_3
    };

    private Context context;
    private List<RecetaConAutor> trendingRecipes;

    public AdapterTrending(Context context, List<RecetaConAutor> trendingRecipes) {
        this.context = context;
        this.trendingRecipes = trendingRecipes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.tarjeta_amigos, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecetaConAutor recipe = trendingRecipes.get(position);

        Glide.with(context).load(recipe.getReceta().getUrl_foto()).into(holder.recipeImage);
        holder.recipeName.setText(recipe.getReceta().getNombre());
        Glide.with(context).load(recipe.getUrlFotoPerfilAutor()).circleCrop().into(holder.authorImage);

        int tipoIndex = Math.min(recipe.getReceta().getTipo(), TIPO_ICONS.length - 1);
        holder.typeImage.setImageResource(TIPO_ICONS[tipoIndex]);

        holder.itemView.setOnClickListener(v -> openRecipeDetail(recipe));
    }

    @Override
    public int getItemCount() {
        return trendingRecipes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView recipeImage, typeImage, authorImage;
        TextView recipeName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.tarjeta_amigos_foto);
            typeImage = itemView.findViewById(R.id.tarjeta_amigos_type);
            authorImage = itemView.findViewById(R.id.tarjeta_amigos_pfp);
            recipeName = itemView.findViewById(R.id.tarjeta_amigos_text);
        }
    }

    private void openRecipeDetail(RecetaConAutor recipe) {
        Intent intent = new Intent(context, RecetaDetalleActivity.class);
        intent.putExtra("receta", recipe);
        context.startActivity(intent);
    }
}
