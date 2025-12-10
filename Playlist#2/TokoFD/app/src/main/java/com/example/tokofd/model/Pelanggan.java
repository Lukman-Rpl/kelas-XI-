package com.example.tokofd.model;

public class Pelanggan {
    private String username;
    private String account;
    private String password;

    public Pelanggan() {
        // Required empty constructor
    }

    public Pelanggan(String username, String account, String password) {
        this.username = username;
        this.account = account;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }
}
