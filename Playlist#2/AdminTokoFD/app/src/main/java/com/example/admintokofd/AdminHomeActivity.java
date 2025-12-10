package com.example.admintokofd;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AdminHomeActivity extends AppCompatActivity {

    private Button btnKelolaProduk, btnLihatPesanan, btnLihatPelanggan, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        btnKelolaProduk = findViewById(R.id.btnKelolaProduk);
        btnLihatPesanan = findViewById(R.id.btnLihatPesanan);
        btnLihatPelanggan = findViewById(R.id.btnLihatPelanggan); // Tambahan
        btnLogout = findViewById(R.id.btnLogout);

        btnKelolaProduk.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomeActivity.this, ProdukActivity.class);
            startActivity(intent);
        });

        btnLihatPesanan.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomeActivity.this, LihatPesananActivity.class);
            startActivity(intent);
        });

        btnLihatPelanggan.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomeActivity.this, ListPelangganActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Berhasil logout", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AdminHomeActivity.this, LoginActivity.class));
            finish();
        });
    }
}
