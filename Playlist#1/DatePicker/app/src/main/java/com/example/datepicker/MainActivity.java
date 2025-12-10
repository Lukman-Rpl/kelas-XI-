package com.example.datepicker;

import android.app.DatePickerDialog;
// Menggunakan java.util.Calendar untuk menghindari kebingungan jika ada impor ganda
// Meskipun android.icu.util.Calendar yang Anda impor sebelumnya juga bisa bekerja
import java.util.Calendar;
import java.util.Locale;
import java.text.SimpleDateFormat;

import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    TextView etTanggal;
    // Variabel 'Calender' (dengan C besar) masih dideklarasikan tapi tidak digunakan.
    // Jika tidak ada rencana penggunaan, ini bisa dihapus untuk kebersihan kode.
    private java.util.Calendar Calender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // PANGGIL FUNGSI load() DI SINI untuk menginisialisasi etTanggal
        load();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void load(){
        // Pastikan ID 'R.id.etTanggal' ada di file layout activity_main.xml Anda
        etTanggal = findViewById(R.id.etTanggal);
    }

    // Method ini akan dipanggil jika Anda memiliki android:onClick="etTanggal" pada sebuah View di XML
    public void etTanggal(View view) {
        // Jika Anda mengimpor android.icu.util.Calendar dan ingin menggunakannya,
        // baris ini sudah benar. Jika ingin java.util.Calendar, ubah impornya.
        // Di sini saya asumsikan Anda ingin menggunakan Calendar yang diimpor (awalnya android.icu.util.Calendar)
        // atau kita bisa menggantinya ke java.util.Calendar untuk konsistensi.
        // Untuk contoh ini, saya akan menggunakan java.util.Calendar.
        final Calendar cal = Calendar.getInstance(); // Menggunakan java.util.Calendar
        int tgl = cal.get(Calendar.DAY_OF_MONTH);
        int bulan = cal.get(Calendar.MONTH); // bulan berbasis 0 (Januari = 0)
        int tahun = cal.get(Calendar.YEAR);

        DatePickerDialog dtp = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int tahun, int bulanDariDialog, int tgl) {
                        // bulanDariDialog adalah berbasis 0 (Januari = 0)
                        // Untuk menampilkannya sebagai 1-12, kita perlu memformatnya dengan benar.

                        // Membuat instance Calendar untuk tanggal yang dipilih
                        Calendar selectedCalendar = Calendar.getInstance();
                        selectedCalendar.set(tahun, bulanDariDialog, tgl);

                        // Menggunakan SimpleDateFormat untuk format tanggal yang benar dan aman
                        // Format "dd-MM-yyyy" akan menampilkan bulan sebagai 1-12
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        String tanggalTerformat = sdf.format(selectedCalendar.getTime());

                        // PERBAIKAN UTAMA: Pastikan etTanggal tidak null sebelum digunakan
                        if (etTanggal != null) {
                            etTanggal.setText(tanggalTerformat);
                        } else {
                            // Tambahkan log atau penanganan jika etTanggal masih null,
                            // meskipun seharusnya tidak terjadi jika load() dipanggil dengan benar.
                            // android.util.Log.e("MainActivity", "etTanggal is null in onDateSet");
                        }
                    }
                }, tahun, bulan, tgl); // Menggunakan variabel tahun, bulan, tgl awal untuk DatePickerDialog

        dtp.show();
    }
}
