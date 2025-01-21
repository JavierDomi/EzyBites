package com.dam.ezybites.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.dam.ezybites.R;
import com.dam.ezybites.RecetaDetalleActivity;
import com.dam.ezybites.pojos.Receta;
import com.dam.ezybites.pojos.RecetaConAutor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Document;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecetasGuardadasAdapter extends RecyclerView.Adapter<RecetasGuardadasAdapter.ItemVH> {

    private Context context;
    private List<RecetaConAutor> recetasList;

    public RecetasGuardadasAdapter(Context context, List<RecetaConAutor> recetasList) {
        this.context = context;
        this.recetasList = recetasList;
    }

    @NonNull
    @Override
    public ItemVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_receta_guardada, parent, false);
        return new ItemVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemVH holder, int position) {
        RecetaConAutor recetaConAutor = recetasList.get(position);

        Glide.with(context).load(recetaConAutor.getReceta().getUrl_foto()).into(holder.fotoReceta);
        holder.tituloReceta.setText(recetaConAutor.getReceta().getNombre());
        holder.duracionReceta.setText(recetaConAutor.getReceta().getDuracion());
        holder.tipoReceta.setText(getTipoReceta(recetaConAutor.getReceta().getTipo()));

        holder.botonDetalle.setOnClickListener(v -> {
            Intent intent = new Intent(context, RecetaDetalleActivity.class);
            intent.putExtra("receta", recetaConAutor);
            context.startActivity(intent);
        });

        holder.botonDescargarPDF.setOnClickListener(v ->
                descargarRecetaPDF(
                        context,
                        recetaConAutor.getReceta().getUrl_foto(),
                        recetaConAutor.getReceta().getNombre(),
                        recetaConAutor.getReceta().getDuracion(),
                        recetaConAutor.getReceta().getTipo(),
                        recetaConAutor.getReceta()
                )
        );
    }

    @Override
    public int getItemCount() {
        return recetasList.size();
    }

    private String getTipoReceta(int tipo) {
        switch (tipo) {
            case 1:
                return "Vegano";
            case 2:
                return "Vegetariano";
            case 3:
                return "Con carne";
            default:
                return "No especificado";
        }
    }

    public void descargarRecetaPDF(Context context, String imageUrl, String title, String duration, int type, Receta receta) {
        String pdfFileName = title + ".pdf";
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File pdfFile = new File(downloadsDir, pdfFileName);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Document document = new Document();
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(pdfFile);
                PdfWriter.getInstance(document, fileOutputStream);
                document.open();

                // Fuentes
                Font fontTitle = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD); // Título principal en negrita
                Font fontDetailsBold = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD); // Títulos (Duración, Dificultad, etc.)
                Font fontDetailsNormal = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL); // Detalles (valores como "5 minutos", "Media")

                // Título principal
                Paragraph paragraphTitle = new Paragraph(title, fontTitle);
                paragraphTitle.setAlignment(Element.ALIGN_CENTER);
                document.add(paragraphTitle);

                document.add(new Paragraph("\n\n")); // Espacio después del título

                // Duración en negrita
                Paragraph paragraphDuration = new Paragraph("Duración: ", fontDetailsBold);
                paragraphDuration.setAlignment(Element.ALIGN_LEFT);
                document.add(paragraphDuration);

                // Duración en normal
                Paragraph paragraphDurationValue = new Paragraph(duration, fontDetailsNormal);
                document.add(paragraphDurationValue);

                document.add(new Paragraph("\n")); // Espacio después de la duración

                // Tipo de receta en negrita
                String tipoReceta = getTipoReceta(type);
                Paragraph paragraphType = new Paragraph("Tipo: ", fontDetailsBold);
                paragraphType.setAlignment(Element.ALIGN_LEFT);
                document.add(paragraphType);

                // Tipo en normal
                Paragraph paragraphTypeValue = new Paragraph(tipoReceta, fontDetailsNormal);
                document.add(paragraphTypeValue);

                document.add(new Paragraph("\n")); // Espacio después del tipo

                // Dificultad en negrita
                Paragraph paragraphDifficulty = new Paragraph("Dificultad: ", fontDetailsBold);
                document.add(paragraphDifficulty);

                // Dificultad en normal
                Paragraph paragraphDifficultyValue = new Paragraph(receta.getDificultad(), fontDetailsNormal);
                document.add(paragraphDifficultyValue);

                document.add(new Paragraph("\n")); // Espacio después de la dificultad

                // Ingredientes en negrita
                Paragraph paragraphIngredients = new Paragraph("Ingredientes: ", fontDetailsBold);
                document.add(paragraphIngredients);

                document.add(new Paragraph("\n")); // Espacio antes de los ingredientes

                // Ingredientes en normal
                for (String ingredient : receta.getIngredientes()) {
                    Paragraph ingredientParagraph = new Paragraph(" - " + ingredient, fontDetailsNormal);
                    document.add(ingredientParagraph);
                }

                document.add(new Paragraph("\n")); // Espacio después de los ingredientes

                // Pasos en negrita
                Paragraph paragraphSteps = new Paragraph("Pasos: ", fontDetailsBold);
                document.add(paragraphSteps);

                document.add(new Paragraph("\n")); // Espacio antes de los pasos

                // Pasos en normal
                Paragraph stepsParagraph = new Paragraph(receta.getPasos(), fontDetailsNormal);
                document.add(stepsParagraph);

                document.add(new Paragraph("\n")); // Espacio después de los pasos

                // Agregar el logo a la derecha
                try {
                    @SuppressLint("ResourceType") Image logoImage = Image.getInstance(context.getResources().openRawResource(R.drawable.logo).toString());
                    logoImage.scaleToFit(100, 100);
                    logoImage.setAlignment(Image.ALIGN_RIGHT);
                    document.add(logoImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                document.add(new Paragraph("\n")); // Espacio después del logo

                // Cargar la imagen desde la URL y agregarla al PDF
                Glide.with(context)
                        .asBitmap()
                        .load(imageUrl)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {
                                try {
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    resource.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                    Image image = Image.getInstance(stream.toByteArray());

                                    // Ajustar tamaño de la imagen para que se ajuste al PDF
                                    image.scaleToFit(PageSize.A4.getWidth() - 80, PageSize.A4.getHeight() / 4);
                                    image.setAlignment(Image.ALIGN_CENTER);
                                    document.add(image);

                                    document.close();

                                    new Handler(Looper.getMainLooper()).post(() -> {
                                        Toast.makeText(context, "PDF guardado en: " + pdfFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    new Handler(Looper.getMainLooper()).post(() -> {
                                        Toast.makeText(context, "Error al generar el PDF", Toast.LENGTH_SHORT).show();
                                    });
                                }
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }
                        });

            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(context, "Error al generar el PDF", Toast.LENGTH_SHORT).show();
                });
            }
        });

        executor.shutdown();
    }


    public static class ItemVH extends RecyclerView.ViewHolder {
        ImageView fotoReceta;
        TextView tituloReceta, duracionReceta, tipoReceta;
        Button botonDetalle, botonDescargarPDF;

        public ItemVH(@NonNull View itemView) {
            super(itemView);
            fotoReceta = itemView.findViewById(R.id.foto_receta_guardada);
            tituloReceta = itemView.findViewById(R.id.titulo_receta_guardada);
            duracionReceta = itemView.findViewById(R.id.duracion_receta_guardada);
            tipoReceta = itemView.findViewById(R.id.tipo_receta_guardada);
            botonDetalle = itemView.findViewById(R.id.boton_detalle_receta_guardada);
            botonDescargarPDF = itemView.findViewById(R.id.boton_descargar_pdf);
        }
    }
}
