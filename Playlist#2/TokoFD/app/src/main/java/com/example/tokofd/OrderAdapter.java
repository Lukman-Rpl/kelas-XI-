package com.example.tokofd;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tokofd.model.OrderItem;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<OrderItem> orderList;

    public OrderAdapter(List<OrderItem> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderItem order = orderList.get(position);

        holder.namaBarang.setText(order.getNama_barang());
        holder.jumlahBarang.setText("Jumlah: " + order.getJumlah_barang());
        holder.harga.setText("Harga: Rp " + order.getHarga());
        holder.tanggal.setText("Tanggal: " + order.getTanggal());
        holder.alamat.setText("Alamat: " + order.getAlamat_pengiriman());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView namaBarang, jumlahBarang, harga, tanggal, alamat;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            namaBarang = itemView.findViewById(R.id.orderNamaBarang);
            jumlahBarang = itemView.findViewById(R.id.orderJumlah);
            harga = itemView.findViewById(R.id.orderHarga);
            tanggal = itemView.findViewById(R.id.orderTanggal);
            alamat = itemView.findViewById(R.id.orderAlamat);
        }
    }
}
