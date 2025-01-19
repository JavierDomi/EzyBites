package com.dam.ezybites.API;

import androidx.annotation.NonNull;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.database.*;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.concurrent.ExecutionException;

public class FirebaseToJson {

    public static JsonNode getCleanedRecetasJson() throws ExecutionException, InterruptedException {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference();

        Task<DataSnapshot> task = ref.get();
        DataSnapshot dataSnapshot = Tasks.await(task);

        String jsonString = dataSnapshot.getValue().toString();

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(jsonString);
            return rootNode.get("Recetas");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
