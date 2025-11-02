package com.example.tokofd;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tokofd.model.CartItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> itemList;
    private OnItemRemoveListener removeListener;

    public interface OnItemRemoveListener {
        void onItemRemove(int position);
    }

    public CartAdapter(Context context, List<CartItem> itemList, OnItemRemoveListener removeListener) {
        this.context = context;
        this.itemList = itemList;
        this.removeListener = removeListener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = itemList.get(position);

        holder.name.setText(item.getNama_produk());
        holder.price.setText("Harga: Rp " + item.getHarga());
        holder.qty.setText("Jumlah: " + item.getJumlah());
        holder.subtotal.setText("Subtotal: Rp " + item.getSubtotal());

        holder.btnRemoveItem.setOnClickListener(v -> {
            if (removeListener != null) {
                removeListener.onItemRemove(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, qty, subtotal;
        Button btnRemoveItem;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.cartItemName);
            price = itemView.findViewById(R.id.cartItemPrice);
            qty = itemView.findViewById(R.id.cartItemQty);
            subtotal = itemView.findViewById(R.id.cartItemSubtotal);
            btnRemoveItem = itemView.findViewById(R.id.btnRemoveItem);
        }
    }
}
