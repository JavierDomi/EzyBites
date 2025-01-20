package com.dam.ezybites.ui.discover;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
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
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DiscoverFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private Spinner spinnerFeelings;
    private EditText editTextIngredients;
    private SeekBar seekBarTime;
    private TextView textViewTime;
    private Button buttonSearch;
    private Button buttonClear;
    private RecyclerView recyclerViewRecetas;
    private RecetasAdapter recetasAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover, container, false);

        spinnerFeelings = view.findViewById(R.id.spinner_feelings);
        editTextIngredients = view.findViewById(R.id.edit_text_ingredients);
        seekBarTime = view.findViewById(R.id.seek_bar_time);
        textViewTime = view.findViewById(R.id.text_view_time);
        buttonSearch = view.findViewById(R.id.button_search);
        buttonClear = view.findViewById(R.id.button_clear);
        recyclerViewRecetas = view.findViewById(R.id.recycler_view_recipes);

        populateFeelingsSpinner();
        setupSeekBar();
        setupSearchButton();
        setupClearButton();
        setupRecyclerView();

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
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerFeelings.setAdapter(adapter);
        spinnerFeelings.setOnItemSelectedListener(this);
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

    private void setupClearButton() {
        buttonClear.setOnClickListener(v -> clear());
    }

    private void setupRecyclerView() {
        recyclerViewRecetas.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recetasAdapter = new RecetasAdapter(getContext(), new ArrayList<>());
        recyclerViewRecetas.setAdapter(recetasAdapter);
    }


    private void performSearch() {
        String feeling = spinnerFeelings.getSelectedItem().toString();
        String ingredients = editTextIngredients.getText().toString();
        if (ingredients.isEmpty() || ingredients.equals(""))
            ingredients = "Todos los ingredientes";
        final String finalIngredients = ingredients;
        int time = seekBarTime.getProgress();

        new Thread(() -> {
            try {
                JsonNode recetasNode = FirebaseToJson.getCleanedRecetasJson();
                Log.d("DiscoveryFragment", "Contenido recetasNode: " + recetasNode);
                if (recetasNode != null) {
                    String recetasJson = recetasNode.toString();
                    CGPTQuery cgptQuery = new CGPTQuery();
                    String params = String.format("Feeling: %s, Ingredients: %s, Time: %d minutes", feeling, finalIngredients, time);

                    Log.d("DiscoverFragment", "Iniciando búsqueda con parámetros: " + params);

                    cgptQuery.enviarConsultaChatGPT(recetasJson, params, new CGPTQuery.CGPTResponseCallback() {
                        @Override
                        public void onResponse(String response) {
                            getActivity().runOnUiThread(() -> {
                                Log.d("DiscoverFragment", "Respuesta de ChatGPT: " + response);

                                if (response.isEmpty()) {
                                    Toast.makeText(getContext(), "No se encontraron recetas", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                List<String> recetaIds = Arrays.asList(response.split(","));
                                Log.d("DiscoverFragment", "IDs de recetas encontrados: " + recetaIds);

                                List<Receta> resultados = buscarRecetasPorIds(recetasNode, recetaIds);
                                Log.d("DiscoverFragment", "Número de recetas cargadas: " + resultados.size());

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
        recetasAdapter.setRecetas(recetas);
        recetasAdapter.notifyDataSetChanged();
    }

    private void clear() {
        spinnerFeelings.setSelection(0);
        editTextIngredients.setText("");
        seekBarTime.setProgress(0);
        textViewTime.setText("0 minutos");
        recetasAdapter.setRecetas(new ArrayList<>());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedFeeling = parent.getItemAtPosition(position).toString();
        Log.d("DiscoverFragment", "Sentimiento seleccionado: " + selectedFeeling);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Este método se llama si no se selecciona nada
    }
}
