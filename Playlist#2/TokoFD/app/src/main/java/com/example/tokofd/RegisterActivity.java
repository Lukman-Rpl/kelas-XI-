package com.example.tokofd;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference pelangganRef;

    private TextInputEditText registerUsernameEdit;
    private TextInputEditText registerEmailEdit;
    private TextInputEditText registerPasswordEdit;
    private MaterialButton registerButton;
    private MaterialButton goToLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        pelangganRef = FirebaseDatabase.getInstance().getReference("pelanggan");

        // Hubungkan ke view layout
        registerUsernameEdit = findViewById(R.id.registerUsernameEdit);
        registerEmailEdit = findViewById(R.id.registerEmailEdit);
        registerPasswordEdit = findViewById(R.id.registerPasswordEdit);
        registerButton = findViewById(R.id.registerButton);
        goToLoginButton = findViewById(R.id.goToLoginButton);

        // Tombol daftar
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = registerUsernameEdit.getText().toString().trim();
                String email = registerEmailEdit.getText().toString().trim();
                String password = registerPasswordEdit.getText().toString().trim();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterActivity.this, "Semua kolom wajib diisi", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 1. Buat akun baru di Firebase Authentication
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String uid = auth.getCurrentUser().getUid();

                                // 2. Simpan data pelanggan ke Realtime Database
                                Map<String, String> pelangganData = new HashMap<>();
                                pelangganData.put("username", username);
                                pelangganData.put("account", email);

                                pelangganRef.child(uid).setValue(pelangganData)
                                        .addOnCompleteListener(dbTask -> {
                                            if (dbTask.isSuccessful()) {
                                                Toast.makeText(RegisterActivity.this, "Pendaftaran berhasil!", Toast.LENGTH_SHORT).show();

                                                // 3. Pindah ke halaman utama pelanggan
                                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                                finish();
                                            } else {
                                                Toast.makeText(RegisterActivity.this, "Gagal menyimpan data pelanggan", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            } else {
                                Toast.makeText(RegisterActivity.this, "Gagal daftar: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // Tombol pindah ke login
        goToLoginButton.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }
}
