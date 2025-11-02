package com.example.tokofd;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tokofd.model.CartItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private TextView emptyCartText;
    private RecyclerView cartRecycler;
    private Button checkoutButton;

    private CartAdapter cartAdapter;
    private List<CartItem> cartItems = new ArrayList<>();
    private List<String> productKeys = new ArrayList<>();

    private DatabaseReference cartRef;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        emptyCartText = findViewById(R.id.emptyCartText);
        cartRecycler = findViewById(R.id.cartRecycler);
        checkoutButton = findViewById(R.id.checkoutButton);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        cartRef = FirebaseDatabase.getInstance().getReference("cart").child(userId);

        cartRecycler.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(this, cartItems, position -> {
            // Dapatkan key produk dari posisi
            String productKey = getProductKeyByPosition(position);
            if (productKey != null) {
                // Hapus dari Firebase
                cartRef.child(productKey).removeValue()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(CartActivity.this, "Item dihapus", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(CartActivity.this, "Gagal hapus item", Toast.LENGTH_SHORT).show();
                        });
            }
        });
        cartRecycler.setAdapter(cartAdapter);

        loadCartItems();

        checkoutButton.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "Keranjang kosong, tidak bisa checkout", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                startActivity(intent);
            }
        });

    }

    private void loadCartItems() {
        cartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartItems.clear();
                productKeys.clear();

                if (snapshot.exists()) {
                    for (DataSnapshot itemSnap : snapshot.getChildren()) {
                        CartItem item = itemSnap.getValue(CartItem.class);
                        cartItems.add(item);
                        productKeys.add(itemSnap.getKey());
                    }
                    emptyCartText.setVisibility(View.GONE);
                    cartRecycler.setVisibility(View.VISIBLE);
                } else {
                    emptyCartText.setVisibility(View.VISIBLE);
                    cartRecycler.setVisibility(View.GONE);
                }
                cartAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CartActivity.this, "Gagal mengambil data keranjang", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getProductKeyByPosition(int position) {
        if (position >= 0 && position < productKeys.size()) {
            return productKeys.get(position);
        }
        return null;
    }
}
