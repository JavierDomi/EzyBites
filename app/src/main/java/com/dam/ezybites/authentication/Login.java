package com.dam.ezybites.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dam.ezybites.MainActivity;
import com.dam.ezybites.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    TextInputEditText etEmail;
    TextInputEditText etPassword;
    Button btnLogin;
    ProgressBar progBar;
    TextView tvNewAccount;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progBar = findViewById(R.id.progressBar);
        tvNewAccount = findViewById(R.id.tvNewAccount);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progBar.setVisibility(View.VISIBLE);
                String email = String.valueOf(etEmail.getText()).trim();
                String password = String.valueOf(etPassword.getText()).trim();

                if (TextUtils.isEmpty(email)) {
                    etEmail.setError(getString(R.string.enter_email));
                    progBar.setVisibility(View.GONE);
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Login.this, getString(R.string.enter_email), Toast.LENGTH_SHORT).show();
                    progBar.setVisibility(View.GONE);
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progBar.setVisibility(View.GONE);
                        if(task.isSuccessful()){
                            //FirebaseUser currentUser = mAuth.getCurrentUser();
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Toast.makeText(Login.this, getString(R.string.wrong_uname_password), Toast.LENGTH_SHORT).show();

                        }
                    }
                });

            }
        });

        tvNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), NewAccount.class);
                startActivity(i);
                finish();
            }
        });

    }
}