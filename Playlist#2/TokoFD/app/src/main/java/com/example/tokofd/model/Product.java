package com.example.tokofd.model;

public class Product {
    private String nama_produk;
    private int harga;
    private int stok;
    private String tanggal;
    private String gambar; // Base64

    public Product() {
        // Diperlukan untuk Firebase
    }

    public Product(String nama_produk, int harga, int stok, String tanggal, String gambar) {
        this.nama_produk = nama_produk;
        this.harga = harga;
        this.stok = stok;
        this.tanggal = tanggal;
        this.gambar = gambar;
    }

    public String getNama_produk() {
        return nama_produk;
    }

    public int getHarga() {
        return harga;
    }

    public int getStok() {
        return stok;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getGambar() {
        return gambar;
    }
}
