package com.dam.ezybites.api;

import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.concurrent.ExecutionException;

public class FirebaseToJson {

    public static JsonNode getCleanedRecetasJson() throws ExecutionException, InterruptedException {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        // Obtenemos los datos de Firebase
        Task<DataSnapshot> task = ref.get();
        DataSnapshot dataSnapshot = Tasks.await(task);

        if (dataSnapshot.exists()) {
            try {
                // Utilizamos ObjectMapper para convertir directamente el DataSnapshot a JsonNode
                ObjectMapper mapper = new ObjectMapper();
                String jsonString = mapper.writeValueAsString(dataSnapshot.getValue());
                Log.d("FirebaseToJson", "JSON obtenido: " + jsonString);

                JsonNode rootNode = mapper.readTree(jsonString);

                // Retornamos el nodo "Recetas"
                return rootNode.get("Recetas");
            } catch (Exception e) {
                Log.e("FirebaseToJson", "Error al procesar el JSON: " + e.getMessage(), e);
                return null;
            }
        } else {
            Log.w("FirebaseToJson", "El snapshot no contiene datos.");
            return null;
        }
    }
}
