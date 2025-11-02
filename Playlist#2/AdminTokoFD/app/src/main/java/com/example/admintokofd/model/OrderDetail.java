package com.example.admintokofd.model;

public class OrderDetail {
    private String nama_barang;
    private Integer jumlah_barang;
    private Integer harga;
    private String tanggal;
    private String status;

    // Konstruktor kosong diperlukan untuk Firebase
    public OrderDetail() {
    }

    public OrderDetail(String nama_barang, Integer jumlah_barang, Integer harga, String tanggal, String status) {
        this.nama_barang = nama_barang;
        this.jumlah_barang = jumlah_barang;
        this.harga = harga;
        this.tanggal = tanggal;
        this.status = status;
    }

    public String getNama_barang() {
        return nama_barang;
    }

    public void setNama_barang(String nama_barang) {
        this.nama_barang = nama_barang;
    }

    public Integer getJumlah_barang() {
        return jumlah_barang;
    }

    public void setJumlah_barang(Integer jumlah_barang) {
        this.jumlah_barang = jumlah_barang;
    }

    public Integer getHarga() {
        return harga;
    }

    public void setHarga(Integer harga) {
        this.harga = harga;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
