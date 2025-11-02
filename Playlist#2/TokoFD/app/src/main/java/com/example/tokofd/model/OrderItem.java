package com.example.tokofd.model;

public class OrderItem {
    private String user_id;
    private String nama_barang;
    private int jumlah_barang;
    private int harga;
    private String tanggal;
    private String alamat_pengiriman;

    public OrderItem() {} // Diperlukan untuk Firebase

    public OrderItem(String user_id, String nama_barang, int jumlah_barang, int harga, String tanggal, String alamat_pengiriman) {
        this.user_id = user_id;
        this.nama_barang = nama_barang;
        this.jumlah_barang = jumlah_barang;
        this.harga = harga;
        this.tanggal = tanggal;
        this.alamat_pengiriman = alamat_pengiriman;
    }

    public String getUser_id() { return user_id; }
    public String getNama_barang() { return nama_barang; }
    public int getJumlah_barang() { return jumlah_barang; }
    public int getHarga() { return harga; }
    public String getTanggal() { return tanggal; }
    public String getAlamat_pengiriman() { return alamat_pengiriman; }
}

