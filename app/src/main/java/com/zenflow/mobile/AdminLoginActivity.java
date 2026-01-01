package com.zenflow.mobile;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zenflow.mobile.data.AppDatabase;

public class AdminLoginActivity extends AppCompatActivity {

    private boolean adminAuthed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        AppDatabase.ensureDefaultAdmin(this);

        EditText etAdminUser = findViewById(R.id.etAdminUser);
        EditText etAdminPass = findViewById(R.id.etAdminPass);
        Button btnAdminLogin = findViewById(R.id.btnAdminLogin);

        View registerPanel = findViewById(R.id.registerPanel);
        EditText etNewUsername = findViewById(R.id.etNewUsername);
        EditText etNewPassword = findViewById(R.id.etNewPassword);
        Button btnRegisterUser = findViewById(R.id.btnRegisterUser);

        registerPanel.setAlpha(0.4f);
        registerPanel.setEnabled(false);
        setEnabledRecursive(registerPanel, false);

        btnAdminLogin.setOnClickListener(v -> {
            String u = etAdminUser.getText() == null ? null : etAdminUser.getText().toString().trim();
            String p = etAdminPass.getText() == null ? null : etAdminPass.getText().toString();

            if (TextUtils.isEmpty(u) || TextUtils.isEmpty(p)) {
                Toast.makeText(this, "Enter admin username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!AppDatabase.validateAdmin(this, u, p)) {
                Toast.makeText(this, "Invalid admin credentials", Toast.LENGTH_SHORT).show();
                return;
            }

            adminAuthed = true;
            registerPanel.setAlpha(1f);
            registerPanel.setEnabled(true);
            setEnabledRecursive(registerPanel, true);
            Toast.makeText(this, "Admin unlocked. You can register users.", Toast.LENGTH_SHORT).show();
        });

        btnRegisterUser.setOnClickListener(v -> {
            if (!adminAuthed) {
                Toast.makeText(this, "Login as admin first", Toast.LENGTH_SHORT).show();
                return;
            }

            String newU = etNewUsername.getText() == null ? null : etNewUsername.getText().toString().trim();
            String newP = etNewPassword.getText() == null ? null : etNewPassword.getText().toString();

            boolean ok = AppDatabase.registerUser(this, newU, newP);
            if (!ok) {
                Toast.makeText(this, "Could not register (empty or username taken)", Toast.LENGTH_SHORT).show();
                return;
            }

            etNewUsername.setText("");
            etNewPassword.setText("");
            Toast.makeText(this, "User registered", Toast.LENGTH_SHORT).show();
        });
    }

    private static void setEnabledRecursive(View root, boolean enabled) {
        root.setEnabled(enabled);
        if (root instanceof android.view.ViewGroup) {
            android.view.ViewGroup vg = (android.view.ViewGroup) root;
            for (int i = 0; i < vg.getChildCount(); i++) {
                setEnabledRecursive(vg.getChildAt(i), enabled);
            }
        }
    }
}
