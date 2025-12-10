package com.example.tokofd;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class CheckoutActivity extends AppCompatActivity {

    private DatabaseReference databaseRef;
    private String userId;
    private String alamatPengiriman = "Jl. Merdeka No. 10"; // bisa diganti dengan input user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        databaseRef = FirebaseDatabase.getInstance().getReference();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        processCheckout();
    }

    private void processCheckout() {
        DatabaseReference cartRef = databaseRef.child("cart").child(userId);

        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot cartSnapshot) {
                if (!cartSnapshot.exists()) {
                    Toast.makeText(CheckoutActivity.this, "Keranjang kosong!", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (DataSnapshot itemSnapshot : cartSnapshot.getChildren()) {
                    String productId = itemSnapshot.getKey();
                    Map<String, Object> cartItem = (Map<String, Object>) itemSnapshot.getValue();

                    if (cartItem == null) continue;

                    String namaProduk = (String) cartItem.get("nama_produk");
                    long jumlah = (long) cartItem.get("jumlah");
                    long harga = (long) cartItem.get("harga");

                    String tanggal = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                    // Simpan ke orders
                    String orderId = databaseRef.child("orders").push().getKey();
                    if (orderId != null) {
                        DatabaseReference orderRef = databaseRef.child("orders").child(orderId);
                        orderRef.setValue(new Order(
                                userId,
                                namaProduk,
                                (int) jumlah,
                                (int) harga,
                                tanggal,
                                alamatPengiriman
                        ));
                    }

                    // Simpan ke order_detail
                    String orderDetailId = "order" + System.currentTimeMillis();
                    DatabaseReference detailRef = databaseRef.child("order_detail").child(userId).child(orderDetailId);
                    detailRef.setValue(new OrderDetail(
                            namaProduk,
                            (int) jumlah,
                            (int) harga,
                            tanggal,
                            "diproses"
                    ));
                }

                // Hapus isi keranjang dan tunggu sampai selesai baru lanjut
                cartRef.removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Tambah notifikasi
                        String notifId = databaseRef.child("notifications").child(userId).push().getKey();
                        if (notifId != null) {
                            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                            NotificationItem notif = new NotificationItem("Pesanan Anda berhasil dilakukan!", today);
                            databaseRef.child("notifications").child(userId).child(notifId).setValue(notif);
                        }

                        // Navigasi ke activity Home (ganti sesuai kebutuhan)
                        Intent intent = new Intent(CheckoutActivity.this, HomeFragment.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                        Toast.makeText(CheckoutActivity.this, "Checkout berhasil!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(CheckoutActivity.this, "Gagal menghapus keranjang.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CheckoutActivity.this, "Gagal melakukan checkout.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Model pesanan untuk orders
    public static class Order {
        public String user_id, nama_barang, tanggal, alamat_pengiriman;
        public int jumlah_barang, harga;

        public Order() {}

        public Order(String user_id, String nama_barang, int jumlah_barang, int harga, String tanggal, String alamat_pengiriman) {
            this.user_id = user_id;
            this.nama_barang = nama_barang;
            this.jumlah_barang = jumlah_barang;
            this.harga = harga;
            this.tanggal = tanggal;
            this.alamat_pengiriman = alamat_pengiriman;
        }
    }

    // Model untuk order_detail
    public static class OrderDetail {
        public String nama_barang, tanggal, status;
        public int jumlah_barang, harga;

        public OrderDetail() {}

        public OrderDetail(String nama_barang, int jumlah_barang, int harga, String tanggal, String status) {
            this.nama_barang = nama_barang;
            this.jumlah_barang = jumlah_barang;
            this.harga = harga;
            this.tanggal = tanggal;
            this.status = status;
        }
    }

    // Model untuk notifikasi
    public static class NotificationItem {
        public String pesan, tanggal;

        public NotificationItem() {}

        public NotificationItem(String pesan, String tanggal) {
            this.pesan = pesan;
            this.tanggal = tanggal;
        }
    }
}
