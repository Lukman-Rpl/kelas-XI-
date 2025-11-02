package com.example.admintokofd;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.admintokofd.ListPelangganActivity;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private Button btnKelolaProduk, btnKelolaPelanggan, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnKelolaProduk = findViewById(R.id.btnKelolaProduk);
        btnKelolaPelanggan = findViewById(R.id.btnKelolaPelanggan);
        btnLogout = findViewById(R.id.btnLogout);

        btnKelolaProduk.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProdukActivity.class);
            startActivity(intent);
        });

        btnKelolaPelanggan.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ListPelangganActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });
    }
}
