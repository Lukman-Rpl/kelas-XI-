package com.example.sharedpreference; // Ganti dengan package Anda

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SiswaActivity extends AppCompatActivity {

    private EditText etNamaSiswa;
    private EditText etAlamatSiswa;
    private Button btnSimpanDataSiswa;
    private Button btnTampilDataSiswa; // Tombol baru
    private Button btnKembaliKeHome;  // Tombol baru

    private SharedPreferences sharedPreferencesSiswa;
    private static final String PREF_NAME_SISWA = "data_siswa_pref";
    private static final String KEY_NAMA_SISWA = "nama_siswa";
    private static final String KEY_ALAMAT_SISWA = "alamat_siswa";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_siswa);

        // Gunakan ID root layout yang Anda definisikan di XML (layoutSiswaRoot)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layoutSiswaRoot), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etNamaSiswa = findViewById(R.id.etNamaSiswa);
        etAlamatSiswa = findViewById(R.id.etAlamatSiswa);
        btnSimpanDataSiswa = findViewById(R.id.btnSimpanDataSiswa);
        btnTampilDataSiswa = findViewById(R.id.btnTampilDataSiswa); // Inisialisasi tombol baru
        btnKembaliKeHome = findViewById(R.id.btnKembaliKeHome);   // Inisialisasi tombol baru

        // Inisialisasi SharedPreferences untuk data siswa
        sharedPreferencesSiswa = getSharedPreferences(PREF_NAME_SISWA, Context.MODE_PRIVATE);

        // Muat data siswa terakhir saat activity dibuat (jika ada)
        tampilkanDataSiswa();

        btnSimpanDataSiswa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpanDataSiswa();
            }
        });

        btnTampilDataSiswa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tampilkanDataSiswa();
                Toast.makeText(SiswaActivity.this, "Data siswa dimuat", Toast.LENGTH_SHORT).show();
            }
        });

        btnKembaliKeHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kembaliKeHome();
            }
        });
    }

    private void simpanDataSiswa() {
        String namaSiswa = etNamaSiswa.getText().toString().trim();
        String alamatSiswa = etAlamatSiswa.getText().toString().trim();

        if (!namaSiswa.isEmpty() && !alamatSiswa.isEmpty()) {
            SharedPreferences.Editor editor = sharedPreferencesSiswa.edit();
            editor.putString(KEY_NAMA_SISWA, namaSiswa);
            editor.putString(KEY_ALAMAT_SISWA, alamatSiswa);
            editor.apply();

            Toast.makeText(this, "Data Siswa berhasil disimpan", Toast.LENGTH_SHORT).show();
            // Kosongkan field setelah disimpan
            // etNamaSiswa.setText("");
            // etAlamatSiswa.setText("");
        } else {
            Toast.makeText(this, "Nama dan Alamat Siswa tidak boleh kosong", Toast.LENGTH_SHORT).show();
        }
    }

    private void tampilkanDataSiswa() {
        String namaSiswaSimpanan = sharedPreferencesSiswa.getString(KEY_NAMA_SISWA, "");
        String alamatSiswaSimpanan = sharedPreferencesSiswa.getString(KEY_ALAMAT_SISWA, "");

        etNamaSiswa.setText(namaSiswaSimpanan);
        etAlamatSiswa.setText(alamatSiswaSimpanan);
    }

    private void kembaliKeHome() {
        // Cara 1: Cukup tutup activity saat ini
        finish();

        // Cara 2: Jika Anda ingin memastikan MainActivity ada di atas dan tidak membuat instance baru
        // Intent intent = new Intent(this, MainActivity.class);
        // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        // startActivity(intent);
        // finish(); // Tutup SiswaActivity setelah memulai MainActivity
    }
}
