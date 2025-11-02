package com.example.admintokofd.model;

public class Produk {
    private String nama_produk;
    private int stok;
    private int harga;
    private String tanggal;
    private String gambar;

    // Constructor kosong (wajib untuk Firebase)
    public Produk() {
    }

    // Constructor lengkap
    public Produk(String nama_produk, int stok, int harga, String tanggal, String gambar) {
        this.nama_produk = nama_produk;
        this.stok = stok;
        this.harga = harga;
        this.tanggal = tanggal;
        this.gambar = gambar;
    }

    // Getter dan Setter
    public String getNama_produk() {
        return nama_produk;
    }

    public void setNama_produk(String nama_produk) {
        this.nama_produk = nama_produk;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }
}
