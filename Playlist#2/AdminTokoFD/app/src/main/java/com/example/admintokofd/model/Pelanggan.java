package com.example.admintokofd.model;

public class Pelanggan {
    private String username;
    private String account;

    public Pelanggan() {
        // Diperlukan oleh Firebase
    }

    public Pelanggan(String username, String account) {
        this.username = username;
        this.account = account;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
