package com.example.sharedpreference; // Ganti dengan package Anda

import static androidx.core.content.ContextCompat.startActivity;

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

public class MainActivity extends AppCompatActivity {

    private EditText etBarang;
    private EditText etStok;
    // private Button buttonSimpan; // Tidak perlu jika onClick di XML
    // private Button buttonTampil; // Tidak perlu jika onClick di XML
    // private Button btnKeHalamanSiswa; // Tidak perlu jika onClick di XML
    private SharedPreferences sharedPreferences;

    // Nama file SharedPreferences
    private static final String PREF_NAME = "data_barang_pref"; // Lebih baik gunakan konstanta
    private static final String KEY_BARANG = "barang";
    private static final String KEY_STOK = "stok";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etBarang = findViewById(R.id.etBarang);
        etStok = findViewById(R.id.etStok);
        // buttonSimpan = findViewById(R.id.button); // Tidak perlu jika onClick di XML
        // buttonTampil = findViewById(R.id.button2); // Tidak perlu jika onClick di XML
        // btnKeHalamanSiswa = findViewById(R.id.btnKeHalamanSiswa); // Tidak perlu jika onClick di XML


        // Inisialisasi SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Muat data saat activity dibuat (jika ada)
        loadData();

        // Listener untuk tombol tidak diperlukan lagi jika menggunakan android:onClick di XML
    }

    private void loadData() {
        String namaBarang = sharedPreferences.getString(KEY_BARANG, "");
        float stokBarang = sharedPreferences.getFloat(KEY_STOK, 0f);

        etBarang.setText(namaBarang);
        if (stokBarang > 0f) {
            etStok.setText(String.valueOf(stokBarang));
        } else {
            etStok.setText("");
        }
    }

    // Metode ini akan dipanggil oleh atribut android:onClick="simpan"
    public void simpan(View view) {
        String namaBarang = etBarang.getText().toString().trim();
        String stokString = etStok.getText().toString().trim();

        if (namaBarang.isEmpty() || stokString.isEmpty()) {
            Toast.makeText(this, "Nama barang dan stok tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            float stokValue = Float.parseFloat(stokString);
            if (stokValue <= 0) {
                Toast.makeText(this, "Stok barang harus lebih besar dari 0", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_BARANG, namaBarang);
            editor.putFloat(KEY_STOK, stokValue);
            editor.apply(); // Gunakan apply() untuk operasi asynchronous

            Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show();
            etBarang.setText("");
            etStok.setText("");

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Format stok tidak valid", Toast.LENGTH_SHORT).show();
        }
    }

    // Metode ini akan dipanggil oleh atribut android:onClick="tampil"
    public void tampil(View view) {
        loadData(); // Panggil saja loadData untuk menampilkan
        Toast.makeText(this, "Data dimuat", Toast.LENGTH_SHORT).show();
    }

    // Metode ini akan dipanggil oleh atribut android:onClick="goToHalamanSiswa"
    public void goToHalamanSiswa(View view) {
        Intent intent = new Intent(MainActivity.this, SiswaActivity.class);
        startActivity(intent);
    }
}
