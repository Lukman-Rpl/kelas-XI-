package com.example.tokofd;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tokofd.model.Product;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private SearchView searchView;
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
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        searchView = rootView.findViewById(R.id.searchView);
        recyclerView = rootView.findViewById(R.id.searchRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        productList = new ArrayList<>();
        productIdList = new ArrayList<>();
        adapter = new ProductAdapter(getContext(), productList, productIdList);
        recyclerView.setAdapter(adapter);

        produkRef = FirebaseDatabase.getInstance().getReference("produk");

        loadAllProducts();

        // Listener untuk text search
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query.trim());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText.trim());
                return true;
            }
        });

        return rootView;
    }

    private void loadAllProducts() {
        produkRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                productIdList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Product p = snap.getValue(Product.class);
                    if (p != null) {
                        productList.add(p);
                        productIdList.add(snap.getKey());
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

    private void performSearch(String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            loadAllProducts();
            return;
        }

        produkRef.orderByChild("nama_produk")
                .startAt(keyword)
                .endAt(keyword + "\uf8ff")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productList.clear();
                        productIdList.clear();

                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Product p = snap.getValue(Product.class);
                            if (p != null) {
                                productList.add(p);
                                productIdList.add(snap.getKey());
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Gagal mencari produk", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
