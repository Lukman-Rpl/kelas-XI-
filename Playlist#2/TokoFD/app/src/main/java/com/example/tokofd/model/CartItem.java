package com.example.tokofd.model;

public class CartItem {
    private String nama_produk;
    private int jumlah;
    private int harga;
    private int subtotal;

    public CartItem() {
        // Diperlukan oleh Firebase
    }

    public CartItem(String nama_produk, int jumlah, int harga, int subtotal) {
        this.nama_produk = nama_produk;
        this.jumlah = jumlah;
        this.harga = harga;
        this.subtotal = subtotal;
    }

    public String getNama_produk() {
        return nama_produk;
    }

    public int getJumlah() {
        return jumlah;
    }

    public int getHarga() {
        return harga;
    }

    public int getSubtotal() {
        return subtotal;
    }
}
