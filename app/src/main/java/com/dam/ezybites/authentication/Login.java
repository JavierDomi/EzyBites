package com.dam.ezybites.authentication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dam.ezybites.MainActivity;
import com.dam.ezybites.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class Login extends AppCompatActivity {

    TextInputEditText etEmail;
    TextInputEditText etPassword;
    Button btnLogin;
    ProgressBar progBar;
    TextView tvNewAccount;
    FirebaseAuth mAuth;

    private SharedPreferences prefs;
    private static final String PREF_FORCE_LOGIN = "force_login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);

        if (shouldForceLogin()) {
            mAuth.signOut();
        }

        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
            return;
        }

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progBar = findViewById(R.id.progressBar);
        tvNewAccount = findViewById(R.id.tvNewAccount);

        etPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginUser();
                return true;
            }
            return false;
        });

        btnLogin.setOnClickListener(view -> loginUser());

        tvNewAccount.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), NewAccount.class);
            startActivity(i);
            finish();
        });
    }

    private boolean shouldForceLogin() {
        return prefs.getBoolean(PREF_FORCE_LOGIN, false);
    }

    public void toggleForceLogin(boolean force) {
        prefs.edit().putBoolean(PREF_FORCE_LOGIN, force).apply();
    }

    private void loginUser() {
        progBar.setVisibility(View.VISIBLE);
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!validateInput(email, password)) {
            progBar.setVisibility(View.GONE);
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            progBar.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            } else {
                handleAuthError(task.getException());
            }
        });
    }

    private boolean validateInput(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            etEmail.setError(getString(R.string.enter_email));
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError(getString(R.string.invalid_email));
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.enter_password));
            return false;
        }
        return true;
    }

    private void handleAuthError(Exception exception) {
        if (exception instanceof FirebaseAuthInvalidUserException) {
            Toast.makeText(this, R.string.user_not_found, Toast.LENGTH_SHORT).show();
        } else if (exception instanceof FirebaseAuthInvalidCredentialsException) {
            Toast.makeText(this, R.string.invalid_password, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.auth_failed, Toast.LENGTH_SHORT).show();
        }
    }
}
