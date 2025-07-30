package com.example.sqlitedatabase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BarangAdapter extends RecyclerView.Adapter<BarangAdapter.ViewHolder> {

    private Context context;
    private List<Barang> baranglist;

    public BarangAdapter(Context context, List<Barang> baranglist) {
        this.context = context;
        this.baranglist = baranglist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_barang, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Barang barang = baranglist.get(position);

        holder.tvBarang.setText(barang.getBarang());
        holder.tvStok.setText(String.valueOf(barang.getStok()));
        holder.tvHarga.setText(String.valueOf(barang.getHarga()));

        holder.tvMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, holder.tvMenu);
                popupMenu.inflate(R.menu.menu_item);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemId = item.getItemId();

                        if (itemId == R.id.ubah) {
                            ((MainActivity) context).ubah(barang.getId());
                            return true;
                        } else if (itemId == R.id.hapus) {
                            ((MainActivity) context).hapus(barang.getId());
                            return true;
                        } else {
                            return false;
                        }
                    }
                });

                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return baranglist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvBarang, tvStok, tvHarga, tvMenu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBarang = itemView.findViewById(R.id.tvBarang);
            tvStok = itemView.findViewById(R.id.tvStok);
            tvHarga = itemView.findViewById(R.id.tvHarga);
            tvMenu = itemView.findViewById(R.id.tvMenu);
        }
    }
}
