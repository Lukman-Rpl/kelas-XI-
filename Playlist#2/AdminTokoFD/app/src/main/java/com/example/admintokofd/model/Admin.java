package com.example.admintokofd.model;

public class Admin {
    private String username;
    private String account;  // email
    private String password;
    private String role;

    // Konstruktor kosong untuk Firebase
    public Admin() {
    }

    // Konstruktor lengkap
    public Admin(String username, String account, String password, String role) {
        this.username = username;
        this.account = account;
        this.password = password;
        this.role = role;
    }

    // Getter dan Setter
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
