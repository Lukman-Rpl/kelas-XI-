package com.example.sqlitedatabase;

public class Barang {
    private String id;       // id barang
    private String barang;     // nama barang
    private int stok;        // stok barang
    private double harga;    // harga barang

    // Konstruktor terima semua dalam tipe yang sesuai
    public Barang(String id, String barang, int stok, double harga) {
        this.id = id;
        this.barang = barang;
        this.stok = stok;
        this.harga = harga;
    }

    // Getter
    public String getId() {
        return id;
    }

    public String getBarang() {
        return barang;
    }

    public int getStok() {
        return stok;
    }

    public double getHarga() {
        return harga;
    }

    // Setter
    public void setId(String id) {
        this.id = id;
    }

    public void setbarang(String barang) {
        this.barang = barang;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }

    public void setHarga(double harga) {
        this.harga = harga;
    }
}
