package com.example.sqlitedatabase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "dbtoko";
    private static final int VERSION = 1;

    SQLiteDatabase db;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        db = this.getWritableDatabase();
    }

    // Jalankan query SQL bebas (insert, update, delete, create table, dll)
    public boolean runsql(String sql) {
        try {
            Log.d("DBHelper", "Menjalankan SQL: " + sql);
            db.execSQL(sql);
            return true;
        } catch (Exception e) {
            Log.e("DBHelper", "Error menjalankan SQL", e);
            return false;
        }
    }

    // Ambil data (select) dalam bentuk Cursor
    public Cursor select(String sql) {
        try {
            return db.rawQuery(sql, null);
        } catch (Exception e) {
            Log.e("DBHelper", "Error saat SELECT", e);
            return null;
        }
    }

    // Buat tabel barang jika belum ada
    public void buattable() {
        String tblbarang = "CREATE TABLE IF NOT EXISTS barang (" +
                "idbarang TEXT PRIMARY KEY, " +
                 "barang TEXT, " +
                "stok INTEGER, " +
                "harga REAL" +
                ")";
        runsql(tblbarang);
    }

    // Cek apakah tabel barang kosong
    public boolean isTableBarangEmpty() {
        boolean isEmpty = true;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) AS count FROM barang", null);
            if (cursor != null && cursor.moveToFirst()) {
                int count = cursor.getInt(cursor.getColumnIndexOrThrow("count"));
                isEmpty = (count == 0);
            }
        } catch (Exception e) {
            Log.e("DBHelper", "Error mengecek isi tabel", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return isEmpty;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Kosong karena kita buat tabel manual lewat buattable()
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Kosong untuk saat ini
    }
}
