package com.example.tokofd;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tokofd.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList;
    private List<String> productIdList;

    private DatabaseReference produkRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = rootView.findViewById(R.id.productRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        productList = new ArrayList<>();
        productIdList = new ArrayList<>();
        adapter = new ProductAdapter(getContext(), productList, productIdList);
        recyclerView.setAdapter(adapter);

        produkRef = FirebaseDatabase.getInstance().getReference("produk");
        loadProductsFromFirebase();

        return rootView;
    }

    private void loadProductsFromFirebase() {
        produkRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                productIdList.clear();

                for (DataSnapshot snap : snapshot.getChildren()) {
                    Product p = snap.getValue(Product.class);
                    if (p != null) {
                        productList.add(p);
                        productIdList.add(snap.getKey());  // Simpan ID produk asli dari database
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Gagal memuat produk", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
