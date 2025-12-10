package com.example.tokofd;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.firebase.database.DatabaseReference;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference pelangganRef;

    private TextInputEditText usernameEdit;
    private TextInputEditText passwordEdit;
    private MaterialButton loginButton;
    private MaterialButton goToRegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inisialisasi Firebase
        auth = FirebaseAuth.getInstance();
        pelangganRef = FirebaseDatabase.getInstance().getReference("pelanggan");

        // Hubungkan ke layout
        usernameEdit = findViewById(R.id.usernameEdit); // ini sebenarnya email
        passwordEdit = findViewById(R.id.passwordEdit);
        loginButton = findViewById(R.id.loginButton);
        goToRegisterButton = findViewById(R.id.goToRegisterButton);

        // Tombol Login
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = usernameEdit.getText().toString().trim();
                String password = passwordEdit.getText().toString().trim();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Email dan password wajib diisi", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 1. Login ke Firebase Auth
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String uid = auth.getCurrentUser().getUid();

                                // 2. Cek apakah UID ini ada di node "pelanggan"
                                pelangganRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            String username = snapshot.child("username").getValue(String.class);
                                            Toast.makeText(LoginActivity.this, "Selamat datang, " + username, Toast.LENGTH_SHORT).show();

                                            // TODO: Pindah ke halaman utama pelanggan
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Akun ini bukan pelanggan", Toast.LENGTH_SHORT).show();
                                            auth.signOut();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError error) {
                                        Toast.makeText(LoginActivity.this, "Gagal mengakses database", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } else {
                                Toast.makeText(LoginActivity.this, "Login gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // Tombol Sign Up
        goToRegisterButton.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class); // ganti jika nama activity kamu beda
            startActivity(intent);
        });
    }
}
