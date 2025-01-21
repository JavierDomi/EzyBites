package com.dam.ezybites.ui.discover;

import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dam.ezybites.R;
import com.dam.ezybites.api.CGPTQuery;
import com.dam.ezybites.api.FirebaseToJson;
import com.dam.ezybites.adapters.RecetasAdapter;
import com.dam.ezybites.pojos.Receta;
import com.dam.ezybites.pojos.RecetaConAutor;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DiscoverFragment extends Fragment {

    private EditText editTextFeelings;
    private EditText editTextIngredients;
    private SeekBar seekBarTime;
    private TextView textViewTime;
    private Button buttonSearch;
    private RecyclerView recyclerViewRecetas;
    private RecetasAdapter recetasAdapter;

    private ImageView btnSoloYo, btnComidaFamiliar, btnEvento;
    private ImageView btnFrio, btnCaliente, btnLigero, btnJunk, btnReposteria;
    private boolean isSoloYoSelected, isComidaFamiliarSelected, isEventoSelected;
    private boolean isFrioSelected, isCalienteSelected, isLigeroSelected, isJunkSelected, isReposteriaSelected;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);

        initializeViews(view);
        setupViews();
        setupRecyclerView();

        return view;
    }

    private void initializeViews(View view) {
        editTextFeelings = view.findViewById(R.id.edit_text_feelings);
        editTextIngredients = view.findViewById(R.id.edit_text_ingredients);
        seekBarTime = view.findViewById(R.id.seek_bar_time);
        textViewTime = view.findViewById(R.id.text_view_time);
        buttonSearch = view.findViewById(R.id.button_search);
        recyclerViewRecetas = view.findViewById(R.id.recycler_view_recipes);

        btnSoloYo = view.findViewById(R.id.soloYo);
        btnComidaFamiliar = view.findViewById(R.id.comidaFamiliar);
        btnEvento = view.findViewById(R.id.evento);
        btnFrio = view.findViewById(R.id.frio);
        btnCaliente = view.findViewById(R.id.caliente);
        btnLigero = view.findViewById(R.id.ligero);
        btnJunk = view.findViewById(R.id.junk);
        btnReposteria = view.findViewById(R.id.reposteria);
    }

    private void setupViews() {
        editTextFeelings.setFilters(new InputFilter[] {new InputFilter.LengthFilter(30)});
        setupSeekBar();
        setupSearchButton();
        setupButtons();
    }

    private void setupSeekBar() {
        seekBarTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textViewTime.setText(progress + " minutos");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void setupSearchButton() {
        buttonSearch.setOnClickListener(v -> performSearch());
    }

    private void setupButtons() {
        setupButton(btnSoloYo, "Solo yo", R.drawable.btn_solo_yo, R.drawable.btn_solo_yo_negativo);
        setupButton(btnComidaFamiliar, "En familia", R.drawable.btn_comida_familiar, R.drawable.btn_comida_familiar_negativo);
        setupButton(btnEvento, "Evento", R.drawable.btn_evento, R.drawable.btn_evento_negativo);
        setupButton(btnFrio, "Frío", R.drawable.btn_frio, R.drawable.btn_frio_negativo);
        setupButton(btnCaliente, "Caliente", R.drawable.btn_caliente, R.drawable.btn_caliente_negativo);
        setupButton(btnLigero, "Ligero", R.drawable.btn_ligero, R.drawable.btn_ligero_negativo);
        setupButton(btnJunk, "Junk", R.drawable.btn_junk, R.drawable.btn_junk_negativo);
        setupButton(btnReposteria, "Repostería", R.drawable.btn_reposteria, R.drawable.btn_reposteria_negativo);
    }

    private void setupButton(ImageView button, String tag, int normalDrawable, int selectedDrawable) {
        button.setTag(tag);
        button.setOnClickListener(v -> {
            boolean isSelected = (boolean) v.getTag(R.id.btn_selected_key);
            v.setTag(R.id.btn_selected_key, !isSelected);

            // Cast the View to ImageView
            ImageView imageView = (ImageView) v;

            // Set the appropriate drawable based on selection state
            imageView.setImageResource(!isSelected ? selectedDrawable : normalDrawable);
        });
        button.setTag(R.id.btn_selected_key, false);
    }

    private void setupRecyclerView() {
        recyclerViewRecetas.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recetasAdapter = new RecetasAdapter(getContext(), new ArrayList<>());
        recyclerViewRecetas.setAdapter(recetasAdapter);
    }

    private void performSearch() {
        String feelings = editTextFeelings.getText().toString();
        String ingredients = editTextIngredients.getText().toString();
        int time = seekBarTime.getProgress();

        List<String> selectedParams = new ArrayList<>();
        addIfSelected(selectedParams, btnSoloYo, btnComidaFamiliar, btnEvento);
        addIfSelected(selectedParams, btnFrio, btnCaliente, btnLigero, btnJunk, btnReposteria);

        String params = String.format("Feeling: %s, Ingredients: %s, Time: %d minutes, Selected: %s",
                feelings, ingredients.isEmpty() ? "Todos los ingredientes" : ingredients, time,
                String.join(", ", selectedParams));

        new Thread(() -> {
            try {
                JsonNode recetasNode = FirebaseToJson.getCleanedRecetasJson();
                if (recetasNode != null) {
                    String recetasJson = recetasNode.toString();
                    CGPTQuery cgptQuery = new CGPTQuery();

                    cgptQuery.enviarConsultaChatGPT(recetasJson, params, new CGPTQuery.CGPTResponseCallback() {
                        @Override
                        public void onResponse(String response) {
                            getActivity().runOnUiThread(() -> {
                                if (response.isEmpty()) {
                                    Toast.makeText(getContext(), "No se encontraron recetas", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                List<String> recetaIds = Arrays.asList(response.split(","));
                                List<Receta> resultados = buscarRecetasPorIds(recetasNode, recetaIds);

                                if (resultados.isEmpty()) {
                                    Toast.makeText(getContext(), "No se encontraron recetas coincidentes", Toast.LENGTH_SHORT).show();
                                } else {
                                    actualizarRecyclerView(resultados);
                                }
                            });
                        }

                        @Override
                        public void onError(String error) {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "Error en la búsqueda: " + error, Toast.LENGTH_LONG).show();
                            });
                        }
                    });
                } else {
                    getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error al obtener recetas", Toast.LENGTH_LONG).show());
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void addIfSelected(List<String> params, ImageView... buttons) {
        for (ImageView button : buttons) {
            if ((boolean) button.getTag(R.id.btn_selected_key)) {
                params.add((String) button.getTag());
            }
        }
    }

    private List<Receta> buscarRecetasPorIds(JsonNode recetasNode, List<String> ids) {
        List<Receta> resultados = new ArrayList<>();
        for (String id : ids) {
            JsonNode recetaNode = recetasNode.get(id.trim());
            if (recetaNode != null) {
                Receta receta = new Receta(
                        id,
                        recetaNode.get("autor").asText(),
                        recetaNode.get("dificultad").asText(),
                        recetaNode.get("duracion").asText(),
                        getListFromJsonNode(recetaNode.get("ingredientes")),
                        recetaNode.get("nombre").asText(),
                        recetaNode.get("pasos").asText(),
                        recetaNode.get("rating").asDouble(),
                        getListFromJsonNode(recetaNode.get("referencias")),
                        getListFromJsonNode(recetaNode.get("tags")),
                        recetaNode.get("tipo").asInt(),
                        recetaNode.get("url_foto").asText(),
                        recetaNode.get("visible").asInt()
                );
                resultados.add(receta);
            }
        }
        return resultados;
    }

    private List<String> getListFromJsonNode(JsonNode node) {
        List<String> list = new ArrayList<>();
        if (node != null && node.isArray()) {
            for (JsonNode item : node) {
                list.add(item.asText());
            }
        }
        return list;
    }

    private void actualizarRecyclerView(List<Receta> recetas) {
        List<RecetaConAutor> recetasConAutor = new ArrayList<>();
        for (Receta receta : recetas) {
            recetasConAutor.add(new RecetaConAutor(receta, "", ""));
        }
        recetasAdapter.setRecetas(recetasConAutor);
        recetasAdapter.notifyDataSetChanged();
    }

}

