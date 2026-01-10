package com.zenflow.mobile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zenflow.mobile.analytics.AnalyticsLogger;
import com.zenflow.mobile.auth.SessionManager;
import com.zenflow.mobile.data.AppDatabase;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SessionManager.isLoggedIn(this)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        EditText etUsername = findViewById(R.id.etUsername);
        EditText etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnAdmin = findViewById(R.id.btnAdmin);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText() == null ? null : etUsername.getText().toString().trim();
            String password = etPassword.getText() == null ? null : etPassword.getText().toString();

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Enter username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!AppDatabase.validateUser(this, username, password)) {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                return;
            }

            SessionManager.login(this, username);
            AnalyticsLogger.logLogin(this, "local_db", username == null ? null : username.length());

            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        btnAdmin.setOnClickListener(v -> startActivity(new Intent(this, com.zenflow.mobile.AdminLoginActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        AnalyticsLogger.logScreenView(this, "Login", getClass().getSimpleName());
    }
}
