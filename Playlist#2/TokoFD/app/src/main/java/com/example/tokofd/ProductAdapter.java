package com.example.tokofd;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tokofd.model.Product;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private List<String> productIdList; // List ID produk dari Firebase

    public ProductAdapter(Context context, List<Product> productList, List<String> productIdList) {
        this.context = context;
        this.productList = productList;
        this.productIdList = productIdList;
    }

    public interface OnAddToCartClickListener {
        void onAddToCartClicked(Product product);
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProduct;
        TextView textProductName, textProductPrice, textProductStock, textProductDate;
        MaterialButton buttonAddCart;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProduct = itemView.findViewById(R.id.imageProduct);
            textProductName = itemView.findViewById(R.id.textProductName);
            textProductPrice = itemView.findViewById(R.id.textProductPrice);
            textProductStock = itemView.findViewById(R.id.textProductStock);
            textProductDate = itemView.findViewById(R.id.textProductDate);
            buttonAddCart = itemView.findViewById(R.id.buttonAddCart);
        }
    }

    @NonNull
    @Override
    public ProductAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        String productId = productIdList.get(position); // Ambil ID asli produk dari Firebase

        holder.textProductName.setText(product.getNama_produk());
        holder.textProductPrice.setText("Rp " + product.getHarga());
        holder.textProductStock.setText("Stok: " + product.getStok());
        holder.textProductDate.setText(product.getTanggal());

        // Decode Base64 gambar ke Bitmap
        if (product.getGambar() != null && !product.getGambar().isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(product.getGambar(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.imageProduct.setImageBitmap(decodedByte);
            } catch (IllegalArgumentException e) {
                holder.imageProduct.setImageResource(R.drawable.ic_placeholder_image);
            }
        } else {
            holder.imageProduct.setImageResource(R.drawable.ic_placeholder_image);
        }

        holder.buttonAddCart.setOnClickListener(v -> {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference cartRef = FirebaseDatabase.getInstance()
                    .getReference("cart")
                    .child(userId)
                    .child(productId);

            int jumlah = 1;
            int subtotal = product.getHarga() * jumlah;

            Map<String, Object> cartData = new HashMap<>();
            cartData.put("nama_produk", product.getNama_produk());
            cartData.put("jumlah", jumlah);
            cartData.put("harga", product.getHarga());
            cartData.put("subtotal", subtotal);

            cartRef.setValue(cartData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Produk ditambahkan ke keranjang", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Gagal menambahkan ke keranjang", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}
