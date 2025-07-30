package com.example.kalkulator;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    TextView tvHasil;
    EditText etBill_1,etBill_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        load();
    }

    public void load(){
        tvHasil = findViewById(R.id.tvHasil);
        etBill_1=findViewById(R.id.etBill_1);
        etBill_2=findViewById(R.id.etBill_2);
    }

    public void btnjumlah(View view) {

        if (etBill_1.getText().toString().equals("") || etBill_2.getText().toString().equals("")){
            Toast.makeText(this, "Ada bilangan yang kosong", Toast.LENGTH_SHORT).show();
        }

        double bil_1=Double.parseDouble(etBill_1.getText().toString());
        double bil_2=Double.parseDouble(etBill_2.getText().toString());

        double hasil = bil_1 + bil_2;
        tvHasil.setText(hasil+"");
    }


    public void btnkali(View view) {
        double bil_1=Double.parseDouble(etBill_1.getText().toString());
        double bil_2=Double.parseDouble(etBill_2.getText().toString());

        double hasil = bil_1 * bil_2;
        tvHasil.setText(hasil+"");
    }

    public void btnkurang(View view) {
        double bil_1=Double.parseDouble(etBill_1.getText().toString());
        double bil_2=Double.parseDouble(etBill_2.getText().toString());

        double hasil = bil_1 - bil_2;
        tvHasil.setText(hasil+"");
    }

    public void btnbagi(View view) {
        double bil_1=Double.parseDouble(etBill_1.getText().toString());
        double bil_2=Double.parseDouble(etBill_2.getText().toString());

        double hasil = bil_1 / bil_2;
        tvHasil.setText(hasil+"");
    }
}