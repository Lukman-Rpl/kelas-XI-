package com.example.admintokofd;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admintokofd.model.Pelanggan;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListPelangganActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PelangganAdapter adapter;
    private List<Pelanggan> pelangganList = new ArrayList<>();
    private List<String> uidList = new ArrayList<>();
    private DatabaseReference pelangganRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_form_pelanggan);

        recyclerView = findViewById(R.id.rvPelanggan);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        pelangganRef = FirebaseDatabase.getInstance().getReference("pelanggan");

        loadPelanggan();
    }

    private void loadPelanggan() {
        pelangganRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pelangganList.clear();
                uidList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Pelanggan pelanggan = ds.getValue(Pelanggan.class);
                    if (pelanggan != null) {
                        pelangganList.add(pelanggan);
                        uidList.add(ds.getKey());
                    }
                }
                adapter = new PelangganAdapter(ListPelangganActivity.this, pelangganList, uidList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ListPelangganActivity.this, "Gagal load pelanggan: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
