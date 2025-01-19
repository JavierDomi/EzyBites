package com.dam.ezybites.ui.discover;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.dam.ezybites.API.CGPTQuery;
import com.dam.ezybites.API.FirebaseToJson;
import com.dam.ezybites.R;
import com.dam.ezybites.pojos.Receta;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DiscoverFragment extends Fragment {

    private Spinner spinnerFeelings;
    private EditText editTextIngredients;
    private SeekBar seekBarTime;
    private TextView textViewTime;
    private Button buttonSearch, buttonClear;
    private RecyclerView recyclerViewRecipes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);

        // Initialize all XML elements
        spinnerFeelings = view.findViewById(R.id.spinner_feelings);
        editTextIngredients = view.findViewById(R.id.edit_text_ingredients);
        seekBarTime = view.findViewById(R.id.seek_bar_time);
        textViewTime = view.findViewById(R.id.text_view_time);
        buttonSearch = view.findViewById(R.id.button_search);
        buttonClear = view.findViewById(R.id.button_clear);
        recyclerViewRecipes = view.findViewById(R.id.recycler_view_recipes);

        populateFeelingsSpinner();
        setupSeekBar();
        setupSearchButton();
        setupClearButton();

        return view;
    }

    private void populateFeelingsSpinner() {
        List<String> feelings = Arrays.asList(
                "Feliz", "Triste", "Emocionado", "Cansado", "Estresado",
                "Relajado", "Ansioso", "Motivado", "Aburrido", "Nostálgico",
                "Inspirado", "Frustrado", "Agradecido", "Confundido", "Optimista",
                "Pesimista", "Enérgico", "Perezoso", "Curioso", "Satisfecho",
                "Decepcionado", "Orgulloso", "Culpable", "Esperanzado", "Asustado"
        );

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                feelings
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFeelings.setAdapter(adapter);
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
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSearch();
                clear();
            }
        });
    }

    private void setupClearButton(){
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });
    }

    private void performSearch() {
        String feeling = spinnerFeelings.getSelectedItem().toString();
        String ingredients = editTextIngredients.getText().toString();
        int time = seekBarTime.getProgress();

        new Thread(() -> {
            try {
                JsonNode recetasNode = FirebaseToJson.getCleanedRecetasJson();
                if (recetasNode != null) {
                    // Convertir JsonNode a String
                    String recetasJson = recetasNode.toString();

                    // Crear una instancia de CGPTQuery
                    CGPTQuery cgptQuery = new CGPTQuery();

                    // Preparar los parámetros para la consulta
                    String params = String.format("Feeling: %s, Ingredients: %s, Time: %d minutes", feeling, ingredients, time);

                    // Llamar al método enviarConsultaChatGPT con los datos, parámetros y el callback
                    cgptQuery.enviarConsultaChatGPT(recetasJson, params, new CGPTQuery.CGPTResponseCallback() {
                        @Override
                        public void onResponse(String response) {
                            getActivity().runOnUiThread(() -> {
                                // Procesar la respuesta
                                List<String> recetaIds = Arrays.asList(response.split(","));
                                List<Receta> resultados = buscarRecetasPorIds(recetasNode, recetaIds);
                                actualizarRecyclerView(resultados);
                            });
                        }

                        @Override
                        public void onError(String error) {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_LONG).show();
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

    private List<Receta> buscarRecetasPorIds(JsonNode recetasNode, List<String> ids) {
        List<Receta> resultados = new ArrayList<>();
        for (String id : ids) {
            JsonNode recetaNode = recetasNode.get(id.trim());
            if (recetaNode != null) {
                // Crear objeto Receta a partir del nodo JSON
                Receta receta = new Receta(
                        id,
                        recetaNode.get("nombre").asText(),
                        recetaNode.get("url_foto").asText(),
                        (short)recetaNode.get("tipo").asInt()
                        // Añade más campos según tu clase Receta
                );
                resultados.add(receta);
            }
        }
        return resultados;
    }

    private void actualizarRecyclerView(List<Receta> recetas) {
        // Asumiendo que tienes un adapter llamado recetasAdapter:
        recetasAdapter.setRecetas(recetas);
        recetasAdapter.notifyDataSetChanged();
    }





    private void clear() {
        spinnerFeelings.setSelection(0);
        editTextIngredients.setText("");
        seekBarTime.setProgress(0);

        textViewTime.setText("0 minutos");

        // Clear RecyclerView (assuming you have an adapter set)
        /*if (recyclerViewRecipes.getAdapter() != null) {
            ((ArrayAdapter) recyclerViewRecipes.getAdapter()).clear();
            recyclerViewRecipes.getAdapter().notifyDataSetChanged();
        }*/
    }

}
