package com.example.admintokofd;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.admintokofd.model.OrderDetail;

import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {

    private List<OrderDetail> orderList;

    public OrderDetailAdapter(List<OrderDetail> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pesanan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailAdapter.ViewHolder holder, int position) {
        OrderDetail order = orderList.get(position);
        holder.tvNamaBarang.setText("Nama Barang: " + order.getNama_barang());
        holder.tvTanggal.setText("Tanggal: " + order.getTanggal());
        holder.tvJumlahHarga.setText("Jumlah: " + order.getJumlah_barang() + " | Harga: Rp" + order.getHarga());
        holder.tvStatus.setText("Status: " + order.getStatus());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNamaBarang, tvTanggal, tvJumlahHarga, tvAlamat, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNamaBarang = itemView.findViewById(R.id.tvNamaBarang);
            tvTanggal = itemView.findViewById(R.id.tvTanggal);
            tvJumlahHarga = itemView.findViewById(R.id.tvJumlahHarga);
            tvAlamat = itemView.findViewById(R.id.tvAlamat);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
