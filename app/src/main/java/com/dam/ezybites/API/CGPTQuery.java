package com.dam.ezybites.API;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class CGPTQuery {

    private static final String API_KEY =
            "sk-proj-mqxFNqMKo2TqVb_u2vtF6pEYEOdmaqMGvcjjVrZDVop0OxtISqGj8kn3HQd7DvD-KrhaFmdVvJT3" +
                    "BlbkFJfxmlLbyRDYtDYuohrD3BHzZrMe_YSjJTgckTu2_fm-Hm7zTV2r5ApjLVsCTfyk39_FlcNgBBQA";

    public interface CGPTResponseCallback {
        void onResponse(String response);
        void onError(String error);
    }

    public void enviarConsultaChatGPT(String datos, String params, CGPTResponseCallback callback) {
        new Thread(() -> {
            try {
                URL url = new URL("https://api.openai.com/v1/chat/completions");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
                conn.setRequestProperty("Content-Type", "application/json");

                JSONObject body = new JSONObject();
                body.put("model", "gpt-4");
                body.put("messages", new JSONArray()
                        .put(new JSONObject().put("role", "system").put("content",
                                "Escoje el id de las recetas que concuerden con los parámetros dados. Devuélvelos separados por comas"))
                        .put(new JSONObject().put("role", "user").put("content", datos))
                        .put(new JSONObject().put("role", "user").put("content", params))
                );

                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                os.write(body.toString().getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream is = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // Procesar la respuesta JSON
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    String content = jsonResponse.getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content");

                    callback.onResponse(content);
                } else {
                    callback.onError("HTTP error code: " + responseCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.onError("Error: " + e.getMessage());
            }
        }).start();
    }
}
