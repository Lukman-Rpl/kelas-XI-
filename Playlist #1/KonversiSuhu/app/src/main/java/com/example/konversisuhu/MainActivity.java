package com.example.konversisuhu;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private EditText inputSuhu;
    private Spinner spinnerSuhu;
    private EditText hasilKonversi;
    private Button btnKonversi;
    private TextView textView;

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

        // Inisialisasi komponen UI
        inputSuhu = findViewById(R.id.inputSuhu);
        spinnerSuhu = findViewById(R.id.spinnerSuhu);
        hasilKonversi = findViewById(R.id.hasilKonversi);
        btnKonversi = findViewById(R.id.btnKonversi);
        textView = findViewById(R.id.textView);

        // Mengatur adapter untuk spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.pilihan_konversi, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSuhu.setAdapter(adapter);

        // Mengatur listener untuk tombol konversi
        btnKonversi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                konversiSuhu();
            }
        });
    }

    private void konversiSuhu() {
        // Validasi input
        if (inputSuhu.getText().toString().isEmpty()) {
            Toast.makeText(this, "Masukkan suhu terlebih dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mendapatkan nilai input dan pilihan konversi
        double suhu = Double.parseDouble(inputSuhu.getText().toString());
        int pilihan = spinnerSuhu.getSelectedItemPosition();
        double hasil = 0;

        // Melakukan konversi berdasarkan pilihan
        switch (pilihan) {
            case 0: // Celsius ke Fahrenheit
                hasil = (suhu * 9/5) + 32;
                break;
            case 1: // Celsius ke Kelvin
                hasil = suhu + 273.15;
                break;
            case 2: // Celsius ke Reamur
                hasil = suhu * 4/5;
                break;
            case 3: // Fahrenheit ke Celsius
                hasil = (suhu - 32) * 5/9;
                break;
            case 4: // Fahrenheit ke Kelvin
                hasil = (suhu - 32) * 5/9 + 273.15;
                break;
            case 5: // Fahrenheit ke Reamur
                hasil = (suhu - 32) * 4/9;
                break;
            case 6: // Kelvin ke Celsius
                hasil = suhu - 273.15;
                break;
            case 7: // Kelvin ke Fahrenheit
                hasil = (suhu - 273.15) * 9/5 + 32;
                break;
            case 8: // Kelvin ke Reamur
                hasil = (suhu - 273.15) * 4/5;
                break;
            case 9: // Reamur ke Celsius
                hasil = suhu * 5/4;
                break;
            case 10: // Reamur ke Fahrenheit
                hasil = (suhu * 9/4) + 32;
                break;
            case 11: // Reamur ke Kelvin
                hasil = (suhu * 5/4) + 273.15;
                break;
        }

        // Menampilkan hasil konversi
        hasilKonversi.setText(String.format("%.2f", hasil));
        
        // Update angka 0 di bawah tombol
        textView.setText(String.format("%.0f", hasil));
    }
}