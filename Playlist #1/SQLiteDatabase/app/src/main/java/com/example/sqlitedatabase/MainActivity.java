package com.example.sqlitedatabase;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DBHelper db;
    EditText etbarang, etstok, etharga;
    TextView tvpilihan;
    List<Barang> databarang = new ArrayList<>();
    BarangAdapter adapter;
    RecyclerView rcvbarang;
    String idbarang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        load();
        selectdata();
    }

    private void load() {
        db = new DBHelper(this);
        db.buattable();

        // Jika tabel kosong, isi data awal
        if (db.isTableBarangEmpty()) {
            db.runsql("INSERT INTO barang VALUES ('id1', 'Barang Contoh 1', 10, 50000)");
            db.runsql("INSERT INTO barang VALUES ('id2', 'Barang Contoh 2', 5, 25000)");
        }

        etbarang = findViewById(R.id.etbarang);
        etharga = findViewById(R.id.etharga);
        etstok = findViewById(R.id.etstok);
        tvpilihan = findViewById(R.id.tvpilihan);
        rcvbarang = findViewById(R.id.rcvbarang);

        rcvbarang.setLayoutManager(new LinearLayoutManager(this));
        rcvbarang.setHasFixedSize(true);
    }

    public void simpan(View view) {
        String barang = etbarang.getText().toString().trim();
        String stokStr = etstok.getText().toString().trim();
        String hargaStr = etharga.getText().toString().trim();
        String pilihan = tvpilihan.getText().toString().trim();

        if (barang.isEmpty() || stokStr.isEmpty() || hargaStr.isEmpty()) {
            pesan("Semua field wajib diisi");
            return;
        }

        int stokInt;
        double hargaDouble;

        try {
            stokInt = Integer.parseInt(stokStr);
            hargaDouble = Double.parseDouble(hargaStr);
        } catch (NumberFormatException e) {
            pesan("Stok dan harga harus berupa angka");
            return;
        }

        if (pilihan.equals("insert")) {
            String sql = "INSERT INTO barang VALUES ('" + barang + "', '" + barang + "', " + stokInt + ", " + hargaDouble + ")";
            if (db.runsql(sql)) {
                pesan("Insert berhasil");
                selectdata();
            } else {
                pesan("Insert gagal, kemungkinan ID sudah ada");
            }
        } else {
            String sql = "UPDATE barang SET barang='" + barang + "', stok=" + stokInt + ", harga=" + hargaDouble + " WHERE idbarang='" + idbarang + "'";
            if (db.runsql(sql)) {
                pesan("Data berhasil diupdate");
                selectdata();
            } else {
                pesan("Gagal mengupdate data");
            }
        }

        etbarang.setText("");
        etharga.setText("");
        etstok.setText("");
        tvpilihan.setText("insert");
    }

    public void pesan(String isi) {
        Toast.makeText(this, isi, Toast.LENGTH_SHORT).show();
    }

    public void selectdata() {
        String sql = "SELECT * FROM barang ORDER BY barang ASC";
        Cursor cursor = db.select(sql);

        databarang.clear();

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String idbarang = cursor.getString(cursor.getColumnIndexOrThrow("idbarang"));
                String namaBarang = cursor.getString(cursor.getColumnIndexOrThrow("barang"));
                int stokInt = cursor.getInt(cursor.getColumnIndexOrThrow("stok"));
                double hargaDouble = cursor.getDouble(cursor.getColumnIndexOrThrow("harga"));

                databarang.add(new Barang(idbarang, namaBarang, stokInt, hargaDouble));
            }

            adapter = new BarangAdapter(this, databarang);
            rcvbarang.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            pesan("Data kosong");
        }

        if (cursor != null) cursor.close();
    }

    public void hapus(String id) {
        idbarang = id;

        AlertDialog.Builder al = new AlertDialog.Builder(this);
        al.setTitle("PERINGATAN!");
        al.setMessage("Yakin ingin menghapus?");
        al.setPositiveButton("Ya yakin", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String sql = "DELETE FROM barang WHERE idbarang='" + idbarang + "'";
                if (db.runsql(sql)) {
                    pesan("Hapus berhasil");
                    selectdata();
                } else {
                    pesan("Hapus gagal");
                }
            }
        });
        al.setNegativeButton("Tidak jadi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        al.show(); // ← INI PENTING!
    }

    public void ubah(String id) {
        idbarang = id;
        String sql = "SELECT * FROM barang WHERE idbarang='" + idbarang + "'";
        Cursor cursor = db.select(sql);

        if (cursor != null && cursor.moveToFirst()) {
            etbarang.setText(cursor.getString(cursor.getColumnIndexOrThrow("barang")));
            etstok.setText(cursor.getString(cursor.getColumnIndexOrThrow("stok")));
            etharga.setText(cursor.getString(cursor.getColumnIndexOrThrow("harga")));
            tvpilihan.setText("update");
            cursor.close();
        } else {
            pesan("Data tidak ditemukan");
        }
    }
}
