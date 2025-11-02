package com.example.admintokofd;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.admintokofd.model.OrderDetail;

import java.util.ArrayList;
import java.util.List;

public class LihatPesananActivity extends AppCompatActivity {

    private RecyclerView rvPesanan;
    private OrderDetailAdapter adapter;
    private List<OrderDetail> orderList;

    private DatabaseReference orderRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesanan_list);

        rvPesanan = findViewById(R.id.rvPesanan);
        rvPesanan.setLayoutManager(new LinearLayoutManager(this));

        orderList = new ArrayList<>();
        adapter = new OrderDetailAdapter(orderList);
        rvPesanan.setAdapter(adapter);

        // Akses node order_detail langsung dari root database
        orderRef = FirebaseDatabase.getInstance().getReference("order_detail");

        loadOrderData();
    }

    private void loadOrderData() {
        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot orderSnapshot : userSnapshot.getChildren()) {
                        OrderDetail order = orderSnapshot.getValue(OrderDetail.class);
                        if (order != null) {
                            orderList.add(order);
                        }
                    }
                }

                if (orderList.isEmpty()) {
                    Toast.makeText(LihatPesananActivity.this, "Belum ada pesanan.", Toast.LENGTH_SHORT).show();
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LihatPesananActivity.this, "Gagal memuat data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
