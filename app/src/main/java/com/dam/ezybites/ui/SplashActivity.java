package com.dam.ezybites.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.dam.ezybites.authentication.Login;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Tiempo del splash en milisegundos (ejemplo: 3000ms = 3 segundos)
        int splashTime = 3000;

        new Handler().postDelayed(() -> {
            // Redirigir a la actividad principal
            Intent intent = new Intent(SplashActivity.this, Login.class);
            startActivity(intent);
            finish();
        }, splashTime);
    }
}

